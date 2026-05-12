package com.mango.fakestore.core.network.ext

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.error.DomainError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SafeRetrofitCallExtTest {

    @Test
    fun maps_401_to_unauthorized() = runTest {
        val result = safeRetrofitCall<String> {
            throw HttpException(Response.error<String>(401, "".toResponseBody()))
        }

        assertThat(result).isInstanceOf(Either.Left::class.java)
        assertThat((result as Either.Left).value).isInstanceOf(DomainError.Network.Unauthorized::class.java)
    }

    @Test
    fun maps_403_to_forbidden() = runTest {
        val result = safeRetrofitCall<String> {
            throw HttpException(Response.error<String>(403, "".toResponseBody()))
        }

        assertThat(result).isInstanceOf(Either.Left::class.java)
        assertThat((result as Either.Left).value).isInstanceOf(DomainError.Network.Forbidden::class.java)
    }

    @Test
    fun maps_404_to_not_found() = runTest {
        val result = safeRetrofitCall<String> {
            throw HttpException(Response.error<String>(404, "".toResponseBody()))
        }

        assertThat(result).isInstanceOf(Either.Left::class.java)
        assertThat((result as Either.Left).value).isInstanceOf(DomainError.Network.NotFound::class.java)
    }

    @Test
    fun maps_500_to_server_error() = runTest {
        val result = safeRetrofitCall<String> {
            throw HttpException(Response.error<String>(500, "".toResponseBody()))
        }

        assertThat(result).isInstanceOf(Either.Left::class.java)
        val error = (result as Either.Left).value
        assertThat(error).isInstanceOf(DomainError.Network.Server::class.java)
        assertThat((error as DomainError.Network.Server).httpCode).isEqualTo(500)
    }

    @Test
    fun maps_socket_timeout_to_timeout() = runTest {
        val result = safeRetrofitCall<String> {
            throw SocketTimeoutException("timeout")
        }

        assertThat(result).isInstanceOf(Either.Left::class.java)
        assertThat((result as Either.Left).value).isInstanceOf(DomainError.Network.Timeout::class.java)
    }

    @Test
    fun maps_unknown_host_to_no_connection() = runTest {
        val result = safeRetrofitCall<String> {
            throw UnknownHostException("no host")
        }

        assertThat(result).isInstanceOf(Either.Left::class.java)
        assertThat((result as Either.Left).value).isInstanceOf(DomainError.Network.NoConnection::class.java)
    }

    @Test
    fun maps_serialization_exception_to_parsing() = runTest {
        val result = safeRetrofitCall<String> {
            throw SerializationException("bad json")
        }

        assertThat(result).isInstanceOf(Either.Left::class.java)
        assertThat((result as Either.Left).value).isInstanceOf(DomainError.Network.Parsing::class.java)
    }

    @Test
    fun rethrows_cancellation_exception() = runTest {
        var thrown: Throwable? = null
        try {
            safeRetrofitCall<String> { throw CancellationException("cancelled") }
        } catch (e: CancellationException) {
            thrown = e
        }

        assertThat(thrown).isNotNull()
    }
}
