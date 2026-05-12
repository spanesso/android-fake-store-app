package com.mango.fakestore.core.datastore.crypto

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
private const val KEYSET_ALIAS = "mango_tink_master_key"
private const val PREF_FILE = "mango_tink_prefs"
private const val KEY_URI = "android-keystore://$KEYSET_ALIAS"
private const val TEMPLATE = "AES256_GCM"

open class TinkEncryption(
    private val context: Context?,
) {

    private val aead: Aead by lazy {
        requireNotNull(context) { "Context required for TinkEncryption" }
        AeadConfig.register()
        AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_ALIAS, PREF_FILE)
            .withKeyTemplate(KeyTemplates.get(TEMPLATE))
            .withMasterKeyUri(KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    open fun encrypt(plaintext: String): String {
        val ciphertext = aead.encrypt(plaintext.toByteArray(Charsets.UTF_8), null)
        return Base64.encodeToString(ciphertext, Base64.DEFAULT)
    }

    open fun decrypt(ciphertext: String): String {
        val bytes = Base64.decode(ciphertext, Base64.DEFAULT)
        return String(aead.decrypt(bytes, null), Charsets.UTF_8)
    }
}
