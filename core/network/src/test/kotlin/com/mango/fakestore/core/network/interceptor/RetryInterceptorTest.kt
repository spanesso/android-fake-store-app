package com.mango.fakestore.core.network.interceptor

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class RetryInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor(maxRetries = 3, baseDelayMs = 10L, maxDelayMs = 100L))
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun retries_on_500_with_backoff() {
        server.enqueue(MockResponse().setResponseCode(500))
        server.enqueue(MockResponse().setResponseCode(500))
        server.enqueue(MockResponse().setResponseCode(500))
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val response = client.newCall(Request.Builder().url(server.url("/test")).build()).execute()

        assertThat(response.code).isEqualTo(200)
        assertThat(server.requestCount).isEqualTo(4)
    }

    @Test
    fun does_not_retry_on_400() {
        server.enqueue(MockResponse().setResponseCode(400))

        val response = client.newCall(Request.Builder().url(server.url("/test")).build()).execute()

        assertThat(response.code).isEqualTo(400)
        assertThat(server.requestCount).isEqualTo(1)
    }

    @Test
    fun does_not_retry_on_401() {
        server.enqueue(MockResponse().setResponseCode(401))

        val response = client.newCall(Request.Builder().url(server.url("/test")).build()).execute()

        assertThat(response.code).isEqualTo(401)
        assertThat(server.requestCount).isEqualTo(1)
    }

    @Test
    fun retries_on_408() {
        server.enqueue(MockResponse().setResponseCode(408))
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val response = client.newCall(Request.Builder().url(server.url("/test")).build()).execute()

        assertThat(response.code).isEqualTo(200)
        assertThat(server.requestCount).isEqualTo(2)
    }

    @Test
    fun retries_on_429_with_retry_after() {
        server.enqueue(MockResponse().setResponseCode(429).addHeader("Retry-After", "0"))
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val response = client.newCall(Request.Builder().url(server.url("/test")).build()).execute()

        assertThat(response.code).isEqualTo(200)
        assertThat(server.requestCount).isEqualTo(2)
    }

    @Test
    fun retries_on_503_eventually_succeeds() {
        server.enqueue(MockResponse().setResponseCode(503))
        server.enqueue(MockResponse().setResponseCode(503))
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val response = client.newCall(Request.Builder().url(server.url("/test")).build()).execute()

        assertThat(response.code).isEqualTo(200)
        assertThat(server.requestCount).isEqualTo(3)
    }

    @Test
    fun does_not_retry_non_idempotent_post() {
        server.enqueue(MockResponse().setResponseCode(500))

        val response = client.newCall(
            Request.Builder()
                .url(server.url("/test"))
                .post("body".toRequestBody(null))
                .build()
        ).execute()

        assertThat(response.code).isEqualTo(500)
        assertThat(server.requestCount).isEqualTo(1)
    }

    @Test
    fun retries_post_with_idempotency_key() {
        server.enqueue(MockResponse().setResponseCode(500))
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val response = client.newCall(
            Request.Builder()
                .url(server.url("/test"))
                .addHeader("Idempotency-Key", "key-abc-123")
                .post("body".toRequestBody(null))
                .build()
        ).execute()

        assertThat(response.code).isEqualTo(200)
        assertThat(server.requestCount).isEqualTo(2)
    }

    @Test
    fun exhausts_max_retries_and_returns_last_error_response() {
        repeat(4) { server.enqueue(MockResponse().setResponseCode(503)) }

        val response = client.newCall(Request.Builder().url(server.url("/test")).build()).execute()

        assertThat(response.code).isEqualTo(503)
        assertThat(server.requestCount).isEqualTo(4) // 1 initial + 3 retries
    }
}
