package com.mango.fakestore.core.datastore

import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.datastore.model.AppTheme
import com.mango.fakestore.core.datastore.model.SessionData
import com.mango.fakestore.core.datastore.model.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class MangoDataStoreTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = CoroutineScope(testDispatcher + SupervisorJob())

    private lateinit var dataStore: MangoDataStoreImpl
    private lateinit var tinkEncryption: FakeTinkEncryption

    @Before
    fun setUp() {
        tinkEncryption = FakeTinkEncryption()
        val prefsDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_prefs.preferences_pb") },
        )
        dataStore = MangoDataStoreImpl(prefsDataStore, tinkEncryption, testDispatcher)
    }

    @Test
    fun save_and_read_session_returns_correct_data() = runTest(testDispatcher) {
        val session = SessionData(
            accessToken = "access123",
            refreshToken = "refresh456",
            userId = "user789",
        )

        dataStore.saveSession(session)

        dataStore.sessionFlow.test {
            val result = awaitItem()
            assertThat(result.accessToken).isEqualTo("access123")
            assertThat(result.refreshToken).isEqualTo("refresh456")
            assertThat(result.userId).isEqualTo("user789")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun session_flow_emits_empty_initially() = runTest(testDispatcher) {
        dataStore.sessionFlow.test {
            val initial = awaitItem()
            assertThat(initial).isEqualTo(SessionData.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun clear_session_removes_tokens_preserves_preferences() = runTest(testDispatcher) {
        dataStore.saveSession(SessionData("token", "refresh", "user"))
        dataStore.savePreferences(UserPreferences(theme = AppTheme.DARK))
        dataStore.clearSession()

        dataStore.sessionFlow.test {
            assertThat(awaitItem()).isEqualTo(SessionData.Empty)
            cancelAndIgnoreRemainingEvents()
        }

        dataStore.preferencesFlow.test {
            assertThat(awaitItem().theme).isEqualTo(AppTheme.DARK)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun save_preferences_theme_persists() = runTest(testDispatcher) {
        dataStore.savePreferences(UserPreferences(theme = AppTheme.DARK))

        dataStore.preferencesFlow.test {
            assertThat(awaitItem().theme).isEqualTo(AppTheme.DARK)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun is_authenticated_when_access_token_present() = runTest(testDispatcher) {
        dataStore.saveSession(SessionData(accessToken = "token", refreshToken = null, userId = null))

        dataStore.sessionFlow.test {
            assertThat(awaitItem().isAuthenticated).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun is_not_authenticated_when_no_token() = runTest(testDispatcher) {
        dataStore.sessionFlow.test {
            assertThat(awaitItem().isAuthenticated).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun corruption_handler_resets_to_empty_session() = runTest(testDispatcher) {
        val corruptFile = tmpFolder.newFile("corrupt.preferences_pb")
        corruptFile.writeBytes(ByteArray(100) { 0xFF.toByte() })

        val corruptStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            produceFile = { corruptFile },
        )
        val storeWithCorruption = MangoDataStoreImpl(corruptStore, tinkEncryption, testDispatcher)

        storeWithCorruption.sessionFlow.test {
            assertThat(awaitItem()).isEqualTo(SessionData.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun decrypt_failure_returns_null_token_so_session_appears_empty() = runTest(testDispatcher) {
        val brokenTink = object : com.mango.fakestore.core.datastore.crypto.TinkEncryption(null) {
            override fun encrypt(plaintext: String): String = "enc_$plaintext"
            override fun decrypt(ciphertext: String): String = error("Tink key missing")
        }
        val prefsDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("broken_tink.preferences_pb") },
        )
        val storeWithBrokenTink = MangoDataStoreImpl(prefsDataStore, brokenTink, testDispatcher)

        storeWithBrokenTink.saveSession(SessionData("token", "refresh", "user"))

        storeWithBrokenTink.sessionFlow.test {
            val result = awaitItem()
            assertThat(result.accessToken).isNull()
            assertThat(result.isAuthenticated).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    private class FakeTinkEncryption : com.mango.fakestore.core.datastore.crypto.TinkEncryption(null) {
        override fun encrypt(plaintext: String): String = "enc_$plaintext"
        override fun decrypt(ciphertext: String): String = ciphertext.removePrefix("enc_")
    }
}
