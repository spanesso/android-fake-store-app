package com.mango.fakestore.core.datastore

import com.mango.fakestore.core.datastore.model.SessionData
import com.mango.fakestore.core.datastore.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface MangoDataStore {
    val sessionFlow: Flow<SessionData>
    val preferencesFlow: Flow<UserPreferences>
    suspend fun saveSession(data: SessionData)
    suspend fun clearSession()
    suspend fun savePreferences(prefs: UserPreferences)
}
