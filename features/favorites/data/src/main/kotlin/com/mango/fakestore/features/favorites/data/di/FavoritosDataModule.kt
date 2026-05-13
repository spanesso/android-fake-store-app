package com.mango.fakestore.features.favorites.data.di

import com.mango.fakestore.features.favorites.data.repository.FavoritosRepositoryImpl
import com.mango.fakestore.features.favorites.domain.repository.FavoritosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FavoritosDataModule {
    @Binds
    @Singleton
    fun bindFavoritosRepository(impl: FavoritosRepositoryImpl): FavoritosRepository
}
