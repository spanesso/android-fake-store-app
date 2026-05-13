package com.mango.fakestore.features.auth.data.di

import com.mango.fakestore.features.auth.data.remote.AuthApiService
import com.mango.fakestore.features.auth.data.repository.SesionRepositoryImpl
import com.mango.fakestore.features.auth.domain.repository.SesionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthProvidesModule {

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface AuthBindsModule {

    @Binds
    @Singleton
    fun bindSesionRepository(impl: SesionRepositoryImpl): SesionRepository
}
