package com.mango.fakestore.features.products.data.di

import com.mango.fakestore.features.products.data.repository.ProductosRepositoryImpl
import com.mango.fakestore.features.products.data.remote.ProductosApi
import com.mango.fakestore.features.products.domain.repository.ProductosRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsDataProvidesModule {

    @Provides
    @Singleton
    fun provideProductosApi(retrofit: Retrofit): ProductosApi =
        retrofit.create(ProductosApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsDataBindsModule {

    @Binds
    @Singleton
    abstract fun bindProductosRepository(impl: ProductosRepositoryImpl): ProductosRepository
}
