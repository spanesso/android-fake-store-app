package com.mango.fakestore.core.analytics

import com.mango.fakestore.core.error.DomainError

interface Telemetry {
    fun reportarNoFatal(error: DomainError, contexto: Map<String, String> = emptyMap())
    fun registrarEvento(nombre: String, params: Map<String, Any?> = emptyMap())
    fun iniciarTraza(nombre: String): TraceHandle
    fun setUserId(hashUsuario: String)
    fun setContexto(claves: Map<String, String>)
}

interface TraceHandle {
    fun detener()
}
