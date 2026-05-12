package com.mango.fakestore.core.security.biometric

sealed interface BiometricResult {
    data object Exito : BiometricResult
    data object Cancelado : BiometricResult
    data object BloqueadoTemporalmente : BiometricResult
    data object NoDisponible : BiometricResult
    data class Error(val mensaje: String) : BiometricResult
}
