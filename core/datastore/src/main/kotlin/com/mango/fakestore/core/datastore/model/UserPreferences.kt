package com.mango.fakestore.core.datastore.model

data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationsEnabled: Boolean = true,
)
