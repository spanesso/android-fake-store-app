package com.mango.fakestore.core.common.dispatchers.di

import com.mango.fakestore.core.common.dispatchers.AppDispatchers
import com.mango.fakestore.core.common.dispatchers.DefaultAppDispatchers
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DispatchersModule {
    @Binds
    @Singleton
    abstract fun bindDispatchers(impl: DefaultAppDispatchers): AppDispatchers
}
