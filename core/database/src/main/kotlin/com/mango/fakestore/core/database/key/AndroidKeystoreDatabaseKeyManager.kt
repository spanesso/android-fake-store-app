package com.mango.fakestore.core.database.key

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject

private const val PREFS_FILE = "mango_db_key_prefs"
private const val KEY_PASSPHRASE = "db_passphrase"
private const val PASSPHRASE_BYTE_LENGTH = 32

class AndroidKeystoreDatabaseKeyManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : DatabaseKeyManager {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override fun getOrCreatePassphrase(): ByteArray {
        val stored = encryptedPrefs.getString(KEY_PASSPHRASE, null)
        if (stored != null) return Base64.decode(stored, Base64.DEFAULT)

        val passphrase = ByteArray(PASSPHRASE_BYTE_LENGTH).also { SecureRandom().nextBytes(it) }
        encryptedPrefs.edit().putString(KEY_PASSPHRASE, Base64.encodeToString(passphrase, Base64.DEFAULT)).apply()
        return passphrase
    }

    override fun clearPassphrase() {
        encryptedPrefs.edit().remove(KEY_PASSPHRASE).apply()
    }
}
