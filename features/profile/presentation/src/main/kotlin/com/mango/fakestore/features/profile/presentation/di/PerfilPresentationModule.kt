package com.mango.fakestore.features.profile.presentation.di

import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.features.profile.presentation.mapper.PerfilUiErrorMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PerfilPresentationModule {

    @Provides
    @Singleton
    fun providePerfilUiErrorMapper(
        baseMapper: DomainErrorToUiErrorMapper,
    ): PerfilUiErrorMapper = PerfilUiErrorMapper(baseMapper)
}
