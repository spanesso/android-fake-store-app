package com.mango.fakestore.features.products.presentation.di

import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsPresentationModule {

    @Provides
    @Singleton
    fun provideDomainErrorToUiErrorMapper(): DomainErrorToUiErrorMapper =
        DomainErrorToUiErrorMapper()
}
