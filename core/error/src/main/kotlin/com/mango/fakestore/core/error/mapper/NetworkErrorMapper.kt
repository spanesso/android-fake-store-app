package com.mango.fakestore.core.error.mapper

import com.mango.fakestore.core.error.DomainError
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

private const val HTTP_UNAUTHORIZED = 401
private const val HTTP_FORBIDDEN = 403
private const val HTTP_NOT_FOUND = 404
private const val HTTP_SERVER_ERROR_MIN = 500
private const val HTTP_SERVER_ERROR_MAX = 599

class NetworkErrorMapper {
    fun map(throwable: Throwable): DomainError.Network = when (throwable) {
        is SocketTimeoutException -> DomainError.Network.Timeout(throwable)
        is UnknownHostException -> DomainError.Network.NoConnection(throwable)
        is IOException -> DomainError.Network.NoConnection(throwable)
        is TimeoutException -> DomainError.Network.Timeout(throwable)
        is SerializationException -> DomainError.Network.Parsing(throwable)
        else -> mapHttpException(throwable)
    }

    private fun mapHttpException(throwable: Throwable): DomainError.Network {
        val code = extractHttpCode(throwable)
        return when (code) {
            HTTP_UNAUTHORIZED -> DomainError.Network.Unauthorized(throwable)
            HTTP_FORBIDDEN -> DomainError.Network.Forbidden(throwable)
            HTTP_NOT_FOUND -> DomainError.Network.NotFound(throwable)
            in HTTP_SERVER_ERROR_MIN..HTTP_SERVER_ERROR_MAX -> DomainError.Network.Server(code, throwable)
            else -> DomainError.Network.NoConnection(throwable)
        }
    }

    private fun extractHttpCode(throwable: Throwable): Int {
        val message = throwable.message ?: return -1
        return Regex("HTTP (\\d{3})").find(message)?.groupValues?.get(1)?.toIntOrNull() ?: -1
    }
}
