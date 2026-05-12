package com.mango.fakestore.core.analytics

import java.util.UUID

interface SessionIdProvider {
    fun obtener(): String
}

class RandomSessionIdProvider : SessionIdProvider {
    private val id: String = UUID.randomUUID().toString()
    override fun obtener(): String = id
}
