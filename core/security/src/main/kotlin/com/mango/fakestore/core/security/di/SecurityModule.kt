package com.mango.fakestore.core.security.di

import com.mango.fakestore.core.security.biometric.BiometricAuthenticator
import com.mango.fakestore.core.security.biometric.BiometricAuthenticatorImpl
import com.mango.fakestore.core.security.integrity.IntegrityChecker
import com.mango.fakestore.core.security.integrity.IntegrityCheckerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindBiometricAuthenticator(
        impl: BiometricAuthenticatorImpl,
    ): BiometricAuthenticator

    @Binds
    @Singleton
    abstract fun bindIntegrityChecker(
        impl: IntegrityCheckerImpl,
    ): IntegrityChecker
}
