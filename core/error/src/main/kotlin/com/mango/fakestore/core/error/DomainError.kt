package com.mango.fakestore.core.error

sealed interface DomainError {
    val cause: Throwable?

    sealed interface Network : DomainError {
        data class NoConnection(override val cause: Throwable? = null) : Network
        data class Timeout(override val cause: Throwable? = null) : Network
        data class Server(val httpCode: Int, override val cause: Throwable? = null) : Network
        data class Unauthorized(override val cause: Throwable? = null) : Network
        data class Forbidden(override val cause: Throwable? = null) : Network
        data class NotFound(override val cause: Throwable? = null) : Network
        data class Parsing(override val cause: Throwable? = null) : Network
    }

    sealed interface Database : DomainError {
        data class ReadFailed(override val cause: Throwable? = null) : Database
        data class WriteFailed(override val cause: Throwable? = null) : Database
        data class NotFound(override val cause: Throwable? = null) : Database
        data class IntegrityViolation(override val cause: Throwable? = null) : Database
    }

    sealed interface Security : DomainError {
        data object BiometricUnavailable : Security { override val cause: Throwable? = null }
        data object BiometricLockout : Security { override val cause: Throwable? = null }
        data object RootDetected : Security { override val cause: Throwable? = null }
        data object IntegrityFailed : Security { override val cause: Throwable? = null }
        data object SessionExpired : Security { override val cause: Throwable? = null }
    }

    data class Validation(val fields: Map<String, String>) : DomainError {
        override val cause: Throwable? = null
    }

    data class Unknown(override val cause: Throwable? = null) : DomainError
}
