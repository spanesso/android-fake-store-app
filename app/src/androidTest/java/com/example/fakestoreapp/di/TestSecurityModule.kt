package com.example.fakestoreapp.di

import com.mango.fakestore.core.security.biometric.BiometricAuthenticator
import com.mango.fakestore.core.security.biometric.BiometricResult
import com.mango.fakestore.core.security.di.SecurityModule
import com.mango.fakestore.core.security.integrity.IntegrityChecker
import com.mango.fakestore.core.security.integrity.IntegrityResult
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

class FakeBiometricAuthenticator(
    var defaultResult: BiometricResult = BiometricResult.Exito,
) : BiometricAuthenticator {
    override suspend fun autenticar(
        actividad: androidx.fragment.app.FragmentActivity,
        titulo: String,
        subtitulo: String,
        cancelarTexto: String,
    ): BiometricResult = defaultResult
}

class FakeIntegrityChecker(
    var defaultResult: IntegrityResult = IntegrityResult.INTEGRA,
) : IntegrityChecker {
    override fun verificarIntegridad(): IntegrityResult = defaultResult
}

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [SecurityModule::class])
object TestSecurityModule {

    @Provides
    @Singleton
    fun provideBiometricAuthenticator(): BiometricAuthenticator =
        FakeBiometricAuthenticator(defaultResult = BiometricResult.Exito)

    @Provides
    @Singleton
    fun provideIntegrityChecker(): IntegrityChecker =
        FakeIntegrityChecker()
}
