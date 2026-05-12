package com.mango.fakestore.core.network.ext

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.mapper.NetworkErrorMapper
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

suspend fun <T> safeRetrofitCall(block: suspend () -> T): Either<DomainError, T> =
    try {
        Either.Right(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        Either.Left(
            when (e.code()) {
                401 -> DomainError.Network.Unauthorized(e)
                403 -> DomainError.Network.Forbidden(e)
                404 -> DomainError.Network.NotFound(e)
                in 500..599 -> DomainError.Network.Server(e.code(), e)
                else -> DomainError.Network.NoConnection(e)
            }
        )
    } catch (e: Throwable) {
        Either.Left(NetworkErrorMapper().map(e))
    }
