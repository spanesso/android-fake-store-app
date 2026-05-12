package com.mango.fakestore.core.analytics.impl

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.analytics.TraceHandle
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.logging.Logger

class FirebaseTelemetryImpl(
    private val crashlytics: FirebaseCrashlytics,
    private val analytics: FirebaseAnalytics,
    private val performance: FirebasePerformance,
    private val logger: Logger,
) : Telemetry {

    override fun reportarNoFatal(error: DomainError, contexto: Map<String, String>) {
        contexto.forEach { (clave, valor) -> crashlytics.setCustomKey(clave, valor) }
        val causa = error.cause ?: Throwable(error::class.simpleName)
        crashlytics.recordException(causa)
        logger.warn(TAG, "No-fatal registrado: ${error::class.simpleName}", causa)
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

    private companion object {
        const val TAG = "FirebaseTelemetry"
    }
}
