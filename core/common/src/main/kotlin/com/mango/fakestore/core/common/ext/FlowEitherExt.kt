package com.mango.fakestore.core.common.ext

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

fun <L, R, R2> Flow<Either<L, R>>.mapEitherRight(transform: (R) -> R2): Flow<Either<L, R2>> =
    map { either -> either.map(transform) }

fun <L, R> Flow<Either<L, R>>.filterEitherRight(): Flow<R> =
    mapNotNull { either -> either.getOrNull() }
