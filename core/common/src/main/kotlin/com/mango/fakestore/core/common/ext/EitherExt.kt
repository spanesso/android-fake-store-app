package com.mango.fakestore.core.common.ext

import arrow.core.Either

fun <L, R, R2> Either<L, R>.flatMapRight(transform: (R) -> Either<L, R2>): Either<L, R2> =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> transform(value)
    }

fun <L, R, C> Either<L, R>.fold(onLeft: (L) -> C, onRight: (R) -> C): C =
    when (this) {
        is Either.Left -> onLeft(value)
        is Either.Right -> onRight(value)
    }
