package com.mango.fakestore.core.analytics.impl

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.mango.fakestore.core.analytics.ErrorRateLimiter
import com.mango.fakestore.core.analytics.SessionIdProvider
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.analytics.TraceHandle
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.logging.Logger

class FirebaseTelemetryImpl(
    private val crashlytics: FirebaseCrashlytics,
    private val analytics: FirebaseAnalytics,
    private val performance: FirebasePerformance,
    private val logger: Logger,
    private val sessionIdProvider: SessionIdProvider,
    private val rateLimiter: ErrorRateLimiter,
) : Telemetry {

    override fun reportarNoFatal(error: DomainError, contexto: Map<String, String>) {
        // Errores del flujo normal — no son bugs, no se reportan
        if (error is DomainError.Validation || error is DomainError.Security.BiometricLockout) return

        val errorCode = error::class.simpleName ?: "Unknown"
        if (!rateLimiter.permitir(errorCode)) {
            logger.info(TAG, "[Rate-limited] $errorCode")
            return
        }

        val tagsCompletos = contexto + buildMap {
            put("errorType", errorCode)
            put("errorCode", errorCode)
            put("sessionId", sessionIdProvider.obtener())
            if (error is DomainError.Network.Server) put("httpCode", error.httpCode.toString())
        }
        tagsCompletos.forEach { (k, v) -> crashlytics.setCustomKey(k, v) }

        val causa = error.cause ?: Throwable(errorCode)
        crashlytics.recordException(causa)
        logger.warn(TAG, "No-fatal: $errorCode | contexto=$tagsCompletos", causa)
    }

    override fun registrarEvento(nombre: String, params: Map<String, Any?>) {
        val bundle = Bundle()
        params.forEach { (clave, valor) ->
            when (valor) {
                is String -> bundle.putString(clave, valor)
                is Int -> bundle.putInt(clave, valor)
                is Long -> bundle.putLong(clave, valor)
                is Double -> bundle.putDouble(clave, valor)
                is Boolean -> bundle.putBoolean(clave, valor)
                else -> bundle.putString(clave, valor?.toString())
            }
        }
        analytics.logEvent(nombre, bundle)
        logger.info(TAG, "Evento: $nombre | params=$params")
    }

    override fun iniciarTraza(nombre: String): TraceHandle {
        val traza = performance.newTrace(nombre)
        traza.start()
        logger.info(TAG, "Traza iniciada: $nombre")
        return object : TraceHandle {
            override fun detener() {
                traza.stop()
                logger.info(TAG, "Traza detenida: $nombre")
            }
        }
    }

    override fun setUserId(hashUsuario: String) {
        crashlytics.setUserId(hashUsuario)
        logger.info(TAG, "setUserId: [hash]")
    }

    override fun setContexto(claves: Map<String, String>) {
        claves.forEach { (k, v) -> crashlytics.setCustomKey(k, v) }
        logger.info(TAG, "setContexto: keys=${claves.keys}")
    }

    private companion object {
        const val TAG = "FirebaseTelemetry"
    }
}
