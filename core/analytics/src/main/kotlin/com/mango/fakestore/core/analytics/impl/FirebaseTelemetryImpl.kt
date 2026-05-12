package com.mango.fakestore.core.analytics.impl

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.analytics.TraceHandle
import com.mango.fakestore.core.error.DomainError
import timber.log.Timber

class FirebaseTelemetryImpl(
    private val crashlytics: FirebaseCrashlytics,
    private val analytics: FirebaseAnalytics,
    private val performance: FirebasePerformance,
) : Telemetry {

    override fun reportarNoFatal(error: DomainError, contexto: Map<String, String>) {
        contexto.forEach { (clave, valor) -> crashlytics.setCustomKey(clave, valor) }
        val causa = error.cause ?: Throwable(error::class.simpleName)
        crashlytics.recordException(causa)
        Timber.w(causa, "[Analytics] No-fatal: %s", error::class.simpleName)
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
    }

    override fun iniciarTraza(nombre: String): TraceHandle {
        val traza = performance.newTrace(nombre)
        traza.start()
        return object : TraceHandle {
            override fun detener() = traza.stop()
        }
    }
}
