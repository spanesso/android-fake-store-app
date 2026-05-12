package com.mango.fakestore.core.database.di

import com.mango.fakestore.core.database.key.AndroidKeystoreDatabaseKeyManager
import com.mango.fakestore.core.database.key.DatabaseKeyManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// `:core:database` provee la clave de encriptación. La instancia de MangoDatabase
// la ensambla `:app` donde se conocen todas las @Entity de los feature modules.
// Ver: DatabaseMigrations.all y AndroidKeystoreDatabaseKeyManager para el patrón completo.
@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindDatabaseKeyManager(impl: AndroidKeystoreDatabaseKeyManager): DatabaseKeyManager
}
