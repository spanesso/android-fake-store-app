package com.mango.fakestore.core.database.key

interface DatabaseKeyManager {
    fun getOrCreatePassphrase(): ByteArray
    fun clearPassphrase()
}
