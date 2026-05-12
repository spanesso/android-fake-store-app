package com.mango.fakestore.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.mango.fakestore.core.common.dispatchers.AppDispatchers
import com.mango.fakestore.core.datastore.MangoDataStore
import com.mango.fakestore.core.datastore.MangoDataStoreImpl
import com.mango.fakestore.core.datastore.crypto.TinkEncryption
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val DATASTORE_FILE = "mango_preferences"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
        dispatchers: AppDispatchers
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        scope = CoroutineScope(dispatchers.io + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile(DATASTORE_FILE) }
    )

    @Provides
    @Singleton
    fun provideTinkEncryption(@ApplicationContext context: Context): TinkEncryption =
        TinkEncryption(context)

    @Provides
    @Singleton
    fun provideMangoDataStore(
        dataStore: DataStore<Preferences>,
        tink: TinkEncryption,
        dispatchers: AppDispatchers
    ): MangoDataStore = MangoDataStoreImpl(dataStore, tink, dispatchers.io)
}
