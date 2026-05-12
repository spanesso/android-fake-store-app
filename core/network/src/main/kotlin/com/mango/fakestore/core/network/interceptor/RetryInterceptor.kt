package com.mango.fakestore.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import kotlin.math.min
import kotlin.random.Random

private const val HTTP_TOO_MANY_REQUESTS = 429
private const val HTTP_REQUEST_TIMEOUT = 408
private const val HTTP_SERVER_ERROR_FIRST = 500
private const val HTTP_SERVER_ERROR_LAST = 599
private const val MS_PER_SECOND = 1_000L
private const val JITTER_RANGE_MS = 300L

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val baseDelayMs: Long = 500L,
    private val maxDelayMs: Long = 10_000L,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // POST sem Idempotency-Key não é repetível — não retentar
        if (request.method == "POST" && request.header("Idempotency-Key") == null) {
            return chain.proceed(request)
        }

        var attempt = 0
        var lastResponse: Response? = null

        while (attempt <= maxRetries) {
            if (attempt > 0) {
                lastResponse?.close()
                val delay = computeDelay(attempt)
                Timber.d("RetryInterceptor: tentativa $attempt após ${delay}ms")
                Thread.sleep(delay)
            }

            val response = chain.proceed(request)

            if (!shouldRetry(response)) return response

            lastResponse = response
            attempt++
        }

        return lastResponse ?: chain.proceed(request)
    }

    private fun shouldRetry(response: Response): Boolean {
        val code = response.code
        if (code == HTTP_TOO_MANY_REQUESTS) {
            val retryAfterSecs = response.header("Retry-After")?.toLongOrNull()
            if (retryAfterSecs != null && retryAfterSecs > 0) {
                Thread.sleep(retryAfterSecs * MS_PER_SECOND)
            }
            return true
        }
        return code == HTTP_REQUEST_TIMEOUT || code in HTTP_SERVER_ERROR_FIRST..HTTP_SERVER_ERROR_LAST
    }

    private fun computeDelay(attempt: Int): Long {
        val exponential = min(baseDelayMs * (1L shl (attempt - 1)), maxDelayMs)
        val jitter = Random.nextLong(-JITTER_RANGE_MS, JITTER_RANGE_MS)
        return maxOf(0L, exponential + jitter)
    }
}
