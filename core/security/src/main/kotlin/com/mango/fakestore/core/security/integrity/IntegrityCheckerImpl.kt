package com.mango.fakestore.core.security.integrity

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import com.mango.fakestore.core.logging.Logger
import com.scottyab.rootbeer.RootBeer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.FileReader
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Named

class IntegrityCheckerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger,
    private val politica: IntegrityPolicy,
    @Named("expectedCertHash") private val expectedCertHash: String,
) : IntegrityChecker {

    override fun verificarIntegridad(): IntegrityResult {
        val razones = mutableListOf<String>()

        if (esRoot()) razones += "root_detectado"
        if (esDepuradorActivo()) razones += "depurador_activo"
        if (esFridaActivo()) razones += "frida_detectado"
        if (esXposedActivo()) razones += "xposed_detectado"
        if (!esFirmaValida()) razones += "firma_apk_invalida"

        val comprometido = razones.isNotEmpty()
        if (comprometido) {
            logger.warn(TAG, "Integridad comprometida: ${razones.joinToString()}")
        } else {
            logger.info(TAG, "Integridad verificada — sin amenazas detectadas")
        }

        return IntegrityResult(
            estaComprometido = comprometido,
            razones = razones,
            politica = politica,
        )
    }

    private fun esRoot(): Boolean = runCatching { RootBeer(context).isRooted }
        .fold(onSuccess = { it }, onFailure = { false })

    private fun esDepuradorActivo(): Boolean = Debug.isDebuggerConnected()

    private fun esFridaActivo(): Boolean {
        val fridaArtefactos = listOf("frida", "gum-js-loop", "re.frida", "gum-init")
        return try {
            BufferedReader(FileReader("/proc/self/maps")).use { reader ->
                reader.lines().anyMatch { linea ->
                    fridaArtefactos.any { artefacto -> linea.contains(artefacto) }
                }
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun esXposedActivo(): Boolean {
        return try {
            Class.forName("de.robv.android.xposed.XposedBridge")
            true
        } catch (_: ClassNotFoundException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    private fun esFirmaValida(): Boolean {
        if (expectedCertHash.isBlank()) return true

        return try {
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager
                    .getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                    .signingInfo
                    ?.apkContentsSigners
                    ?.toList()
            } else {
                @Suppress("DEPRECATION")
                context.packageManager
                    .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                    .signatures
                    ?.toList()
            } ?: return false

            val md = MessageDigest.getInstance("SHA-256")
            signatures.any { firma ->
                val hash = md.digest(firma.toByteArray())
                android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP) == expectedCertHash
            }
        } catch (_: Exception) {
            false
        }
    }

    private companion object {
        const val TAG = "IntegrityChecker"
    }
}
