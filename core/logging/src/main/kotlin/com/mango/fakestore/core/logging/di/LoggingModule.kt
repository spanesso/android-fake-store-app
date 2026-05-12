package com.mango.fakestore.core.logging.di

import com.mango.fakestore.core.logging.BuildConfig
import com.mango.fakestore.core.logging.Logger
import com.mango.fakestore.core.logging.impl.NoOpLogger
import com.mango.fakestore.core.logging.impl.TimberLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoggingModule {

    @Provides
    @Singleton
    fun provideLogger(): Logger =
        if (BuildConfig.DEBUG) TimberLogger() else NoOpLogger()
}
