package com.mango.fakestore.core.datastore.internal

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object PreferencesKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token_enc")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token_enc")
    val USER_ID = stringPreferencesKey("user_id_enc")
    val THEME = stringPreferencesKey("app_theme")
    val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
}
