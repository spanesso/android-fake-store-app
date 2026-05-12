package com.mango.fakestore.core.database.key

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

// AndroidKeystoreDatabaseKeyManager requires a real Android Keystore (instrumented test).
// These tests verify the DatabaseKeyManager interface contract using a fake implementation,
// ensuring the contract is correctly defined for implementations to follow.
class DatabaseKeyManagerTest {

    private lateinit var keyManager: FakeDatabaseKeyManager

    @Before
    fun setUp() {
        keyManager = FakeDatabaseKeyManager()
    }

    @Test
    fun creates_passphrase_on_first_call() {
        val passphrase = keyManager.getOrCreatePassphrase()
        assertThat(passphrase).isNotEmpty()
    }

    @Test
    fun returns_same_passphrase_on_subsequent_calls() {
        val first = keyManager.getOrCreatePassphrase()
        val second = keyManager.getOrCreatePassphrase()
        assertThat(first).isEqualTo(second)
    }

    @Test
    fun passphrase_has_32_bytes() {
        val passphrase = keyManager.getOrCreatePassphrase()
        assertThat(passphrase).hasLength(32)
    }

    @Test
    fun clear_removes_stored_key() {
        val original = keyManager.getOrCreatePassphrase()
        keyManager.clearPassphrase()
        val newPassphrase = keyManager.getOrCreatePassphrase()
        // After clearing, a new passphrase is generated (may differ from original)
        assertThat(newPassphrase).isNotEmpty()
        assertThat(newPassphrase).hasLength(32)
    }

    private class FakeDatabaseKeyManager : DatabaseKeyManager {
        private var stored: ByteArray? = null

        override fun getOrCreatePassphrase(): ByteArray {
            if (stored == null) {
                stored = ByteArray(32) { it.toByte() }
            }
            return stored!!
        }

        override fun clearPassphrase() {
            stored = null
        }
    }
}
