package com.mango.fakestore.core.analytics.impl

import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.analytics.TraceHandle
import com.mango.fakestore.core.error.DomainError
import timber.log.Timber

class ConsoleTelemetryImpl : Telemetry {

    override fun reportarNoFatal(error: DomainError, contexto: Map<String, String>) {
        Timber.w("[Telemetry] No-fatal: %s | contexto=%s", error::class.simpleName, contexto)
    }

    override fun registrarEvento(nombre: String, params: Map<String, Any?>) {
        Timber.d("[Telemetry] Evento: %s | params=%s", nombre, params)
    }

    override fun iniciarTraza(nombre: String): TraceHandle {
        val inicio = System.currentTimeMillis()
        Timber.d("[Telemetry] Traza iniciada: %s", nombre)
        return object : TraceHandle {
            override fun detener() {
                val ms = System.currentTimeMillis() - inicio
                Timber.d("[Telemetry] Traza detenida: %s (%dms)", nombre, ms)
            }
        }
    }
}
