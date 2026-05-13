package com.mango.fakestore.features.profile.data.di

import com.mango.fakestore.features.profile.data.remote.PerfilApi
import com.mango.fakestore.features.profile.data.repository.PerfilRepositoryImpl
import com.mango.fakestore.features.profile.domain.repository.PerfilRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PerfilDataProvidesModule {

    @Provides
    @Singleton
    fun providePerfilApi(retrofit: Retrofit): PerfilApi =
        retrofit.create(PerfilApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface PerfilDataBindsModule {

    @Binds
    @Singleton
    fun bindPerfilRepository(impl: PerfilRepositoryImpl): PerfilRepository
}
