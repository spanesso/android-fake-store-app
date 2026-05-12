package com.example.fakestoreapp.di

import android.content.Context
import androidx.room.Room
import com.example.fakestoreapp.database.AppDatabase
import com.mango.fakestore.core.database.MangoDatabase
import com.mango.fakestore.features.products.data.local.ProductosDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            MangoDatabase.DATABASE_NAME,
        ).build()

    @Provides
    @Singleton
    fun provideProductosDao(database: AppDatabase): ProductosDao =
        database.productosDao()
}
