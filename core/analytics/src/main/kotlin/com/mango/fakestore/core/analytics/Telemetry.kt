package com.mango.fakestore.core.analytics

import com.mango.fakestore.core.error.DomainError

interface Telemetry {
    fun reportarNoFatal(error: DomainError, contexto: Map<String, String> = emptyMap())
    fun registrarEvento(nombre: String, params: Map<String, Any?> = emptyMap())
    fun iniciarTraza(nombre: String): TraceHandle
}

interface TraceHandle {
    fun detener()
}
