package com.mango.fakestore.core.security.di

import com.mango.fakestore.core.security.integrity.IntegrityChecker
import com.mango.fakestore.core.security.integrity.IntegrityCheckerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SecurityModule {

    @Binds
    @Singleton
    fun bindIntegrityChecker(impl: IntegrityCheckerImpl): IntegrityChecker
}
