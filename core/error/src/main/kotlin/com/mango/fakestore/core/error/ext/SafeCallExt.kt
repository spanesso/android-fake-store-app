package com.mango.fakestore.core.error.ext

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.mapper.DatabaseErrorMapper
import com.mango.fakestore.core.error.mapper.NetworkErrorMapper
import kotlinx.coroutines.CancellationException

private val networkMapper = NetworkErrorMapper()
private val databaseMapper = DatabaseErrorMapper()

@Suppress("TooGenericExceptionCaught")
suspend fun <T> safeApiCall(block: suspend () -> T): Either<DomainError, T> =
    try {
        Either.Right(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Either.Left(networkMapper.map(e))
    }

@Suppress("TooGenericExceptionCaught")
suspend fun <T> safeDbCall(block: suspend () -> T): Either<DomainError, T> =
    try {
        Either.Right(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Either.Left(databaseMapper.map(e))
    }
