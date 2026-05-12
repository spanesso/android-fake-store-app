package com.mango.fakestore.core.security.biometric

import androidx.fragment.app.FragmentActivity

interface BiometricAuthenticator {
    suspend fun autenticar(
        actividad: FragmentActivity,
        titulo: String,
        subtitulo: String,
        cancelarTexto: String,
    ): BiometricResult
}
