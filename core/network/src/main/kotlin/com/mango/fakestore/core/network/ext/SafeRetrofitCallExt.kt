package com.mango.fakestore.core.network.ext

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.mapper.NetworkErrorMapper
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

private const val HTTP_UNAUTHORIZED = 401
private const val HTTP_FORBIDDEN = 403
private const val HTTP_NOT_FOUND = 404
private const val HTTP_SERVER_ERROR_FIRST = 500
private const val HTTP_SERVER_ERROR_LAST = 599

@Suppress("TooGenericExceptionCaught")
suspend fun <T> safeRetrofitCall(block: suspend () -> T): Either<DomainError, T> =
    try {
        Either.Right(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        Either.Left(
            when (e.code()) {
                HTTP_UNAUTHORIZED -> DomainError.Network.Unauthorized(e)
                HTTP_FORBIDDEN -> DomainError.Network.Forbidden(e)
                HTTP_NOT_FOUND -> DomainError.Network.NotFound(e)
                in HTTP_SERVER_ERROR_FIRST..HTTP_SERVER_ERROR_LAST -> DomainError.Network.Server(e.code(), e)
                else -> DomainError.Network.NoConnection(e)
            },
        )
    } catch (e: Throwable) {
        Either.Left(NetworkErrorMapper().map(e))
    }
