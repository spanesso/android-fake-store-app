package com.mango.fakestore.core.datastore.model

data class SessionData(
    val accessToken: String?,
    val refreshToken: String?,
    val userId: String?
) {
    val isAuthenticated: Boolean get() = accessToken != null

    companion object {
        val Empty = SessionData(null, null, null)
    }
}
