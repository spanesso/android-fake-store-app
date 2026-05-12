@file:Suppress("TooManyFunctions")

package com.mango.fakestore.core.testing.builders

import com.mango.fakestore.core.error.DomainError

fun domainErrorNoConnection(causa: Throwable? = null): DomainError.Network.NoConnection =
    DomainError.Network.NoConnection(causa)

fun domainErrorTimeout(causa: Throwable? = null): DomainError.Network.Timeout =
    DomainError.Network.Timeout(causa)

fun domainErrorServer(codigo: Int = 500, causa: Throwable? = null): DomainError.Network.Server =
    DomainError.Network.Server(codigo, causa)

fun domainErrorUnauthorized(causa: Throwable? = null): DomainError.Network.Unauthorized =
    DomainError.Network.Unauthorized(causa)

fun domainErrorForbidden(causa: Throwable? = null): DomainError.Network.Forbidden =
    DomainError.Network.Forbidden(causa)

fun domainErrorNotFound(causa: Throwable? = null): DomainError.Network.NotFound =
    DomainError.Network.NotFound(causa)

fun domainErrorParsing(causa: Throwable? = null): DomainError.Network.Parsing =
    DomainError.Network.Parsing(causa)

fun domainErrorDbLectura(causa: Throwable? = null): DomainError.Database.ReadFailed =
    DomainError.Database.ReadFailed(causa)

fun domainErrorDbEscritura(causa: Throwable? = null): DomainError.Database.WriteFailed =
    DomainError.Database.WriteFailed(causa)

fun domainErrorDbNoEncontrado(causa: Throwable? = null): DomainError.Database.NotFound =
    DomainError.Database.NotFound(causa)

fun domainErrorValidacion(campos: Map<String, String> = emptyMap()): DomainError.Validation =
    DomainError.Validation(campos)

fun domainErrorDesconocido(causa: Throwable? = null): DomainError.Unknown =
    DomainError.Unknown(causa)
