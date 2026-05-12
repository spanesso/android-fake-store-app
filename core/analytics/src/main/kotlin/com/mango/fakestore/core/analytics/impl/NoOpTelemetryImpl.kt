package com.mango.fakestore.core.analytics.impl

import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.analytics.TraceHandle
import com.mango.fakestore.core.error.DomainError

class NoOpTelemetryImpl : Telemetry {
    override fun reportarNoFatal(error: DomainError, contexto: Map<String, String>) = Unit
    override fun registrarEvento(nombre: String, params: Map<String, Any?>) = Unit
    override fun iniciarTraza(nombre: String): TraceHandle = object : TraceHandle {
        override fun detener() = Unit
    }
}
