package com.mango.fakestore.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import kotlin.math.min
import kotlin.random.Random

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val baseDelayMs: Long = 500L,
    private val maxDelayMs: Long = 10_000L
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
        if (code == 429) {
            val retryAfterSecs = response.header("Retry-After")?.toLongOrNull()
            if (retryAfterSecs != null && retryAfterSecs > 0) {
                Thread.sleep(retryAfterSecs * 1_000)
            }
            return true
        }
        return code == 408 || code in 500..599
    }

    private fun computeDelay(attempt: Int): Long {
        val exponential = min(baseDelayMs * (1L shl (attempt - 1)), maxDelayMs)
        val jitter = Random.nextLong(-300L, 300L)
        return maxOf(0L, exponential + jitter)
    }
}
