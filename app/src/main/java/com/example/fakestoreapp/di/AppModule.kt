package com.example.fakestoreapp.di

import android.content.Context
import androidx.room.Room
import com.example.fakestoreapp.BuildConfig
import com.example.fakestoreapp.database.AppDatabase
import com.mango.fakestore.core.database.MangoDatabase
import com.mango.fakestore.core.security.integrity.IntegrityPolicy
import com.mango.fakestore.features.favorites.data.local.FavoritosDao
import com.mango.fakestore.features.products.data.local.ProductosDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
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
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides
    @Singleton
    fun provideProductosDao(database: AppDatabase): ProductosDao =
        database.productosDao()

    @Provides
    @Singleton
    fun provideFavoritosDao(database: AppDatabase): FavoritosDao =
        database.favoritosDao()

    @Provides
    @Singleton
    fun provideIntegrityPolicy(): IntegrityPolicy =
        IntegrityPolicy.valueOf(BuildConfig.INTEGRITY_POLICY)

    @Provides
    @Named("expectedCertHash")
    fun provideExpectedCertHash(): String = BuildConfig.EXPECTED_CERT_HASH
}
