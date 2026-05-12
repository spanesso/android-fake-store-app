package com.mango.fakestore.core.security.biometric

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BiometricAuthenticatorImpl @Inject constructor() : BiometricAuthenticator {

    override suspend fun autenticar(
        actividad: FragmentActivity,
        titulo: String,
        subtitulo: String,
        cancelarTexto: String,
    ): BiometricResult = suspendCoroutine { cont ->
        val executor = ContextCompat.getMainExecutor(actividad)
        var reanudado = false

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (reanudado) return
                reanudado = true
                val resultado = when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_USER_CANCELED -> BiometricResult.Cancelado

                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> BiometricResult.BloqueadoTemporalmente

                    BiometricPrompt.ERROR_HW_UNAVAILABLE,
                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                    BiometricPrompt.ERROR_NO_BIOMETRICS -> BiometricResult.NoDisponible

                    else -> BiometricResult.Error(errString.toString())
                }
                cont.resume(resultado)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                if (reanudado) return
                reanudado = true
                cont.resume(BiometricResult.Exito)
            }

            override fun onAuthenticationFailed() {
                // El usuario puede reintentar; no resumimos la corrutina aquí
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(titulo)
            .setSubtitle(subtitulo)
            .setNegativeButtonText(cancelarTexto)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        BiometricPrompt(actividad, executor, callback).authenticate(promptInfo)
    }
}
