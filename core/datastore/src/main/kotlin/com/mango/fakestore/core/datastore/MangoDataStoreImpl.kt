package com.mango.fakestore.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.mango.fakestore.core.datastore.crypto.TinkEncryption
import com.mango.fakestore.core.datastore.internal.PreferencesKeys
import com.mango.fakestore.core.datastore.model.AppTheme
import com.mango.fakestore.core.datastore.model.SessionData
import com.mango.fakestore.core.datastore.model.UserPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

class MangoDataStoreImpl(
    private val dataStore: DataStore<Preferences>,
    private val tink: TinkEncryption,
    private val ioDispatcher: CoroutineDispatcher,
) : MangoDataStore {

    override val sessionFlow: Flow<SessionData> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            SessionData(
                accessToken = prefs[PreferencesKeys.ACCESS_TOKEN]?.let { decryptOrNull(it, "accessToken") },
                refreshToken = prefs[PreferencesKeys.REFRESH_TOKEN]?.let { decryptOrNull(it, "refreshToken") },
                userId = prefs[PreferencesKeys.USER_ID]?.let { decryptOrNull(it, "userId") },
            )
        }

    override val preferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            UserPreferences(
                theme = prefs[PreferencesKeys.THEME]?.let { raw ->
                    runCatching { AppTheme.valueOf(raw) }
                        .fold(onSuccess = { it }, onFailure = { e ->
                            Timber.e(e, "Unknown AppTheme value: $raw, falling back to SYSTEM")
                            null
                        })
                } ?: AppTheme.SYSTEM,
                notificationsEnabled = prefs[PreferencesKeys.NOTIFICATIONS] ?: true,
            )
        }

    override suspend fun saveSession(data: SessionData) {
        withContext(ioDispatcher) {
            dataStore.edit { prefs ->
                if (data.accessToken != null) {
                    prefs[PreferencesKeys.ACCESS_TOKEN] = tink.encrypt(data.accessToken)
                } else {
                    prefs.remove(PreferencesKeys.ACCESS_TOKEN)
                }
                if (data.refreshToken != null) {
                    prefs[PreferencesKeys.REFRESH_TOKEN] = tink.encrypt(data.refreshToken)
                } else {
                    prefs.remove(PreferencesKeys.REFRESH_TOKEN)
                }
                if (data.userId != null) {
                    prefs[PreferencesKeys.USER_ID] = tink.encrypt(data.userId)
                } else {
                    prefs.remove(PreferencesKeys.USER_ID)
                }
            }
        }
    }

    override suspend fun clearSession() {
        withContext(ioDispatcher) {
            dataStore.edit { prefs ->
                prefs.remove(PreferencesKeys.ACCESS_TOKEN)
                prefs.remove(PreferencesKeys.REFRESH_TOKEN)
                prefs.remove(PreferencesKeys.USER_ID)
            }
        }
    }

    private fun decryptOrNull(ciphertext: String, fieldName: String): String? =
        runCatching { tink.decrypt(ciphertext) }
            .fold(onSuccess = { it }, onFailure = { e ->
                Timber.e(e, "Tink decrypt failed for field '$fieldName'; clearing stored value")
                null
            })

    override suspend fun savePreferences(prefs: UserPreferences) {
        withContext(ioDispatcher) {
            dataStore.edit { store ->
                store[PreferencesKeys.THEME] = prefs.theme.name
                store[PreferencesKeys.NOTIFICATIONS] = prefs.notificationsEnabled
            }
        }
    }
}
