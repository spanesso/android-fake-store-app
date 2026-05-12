package com.mango.fakestore.core.analytics.impl

import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.analytics.TraceHandle
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.logging.Logger

class ConsoleTelemetryImpl(private val logger: Logger) : Telemetry {

    override fun reportarNoFatal(error: DomainError, contexto: Map<String, String>) {
        logger.warn(TAG, "No-fatal: ${error::class.simpleName} | contexto=$contexto", error.cause)
    }

    override fun registrarEvento(nombre: String, params: Map<String, Any?>) {
        logger.info(TAG, "Evento: $nombre | params=$params")
    }

    override fun iniciarTraza(nombre: String): TraceHandle {
        val inicio = System.currentTimeMillis()
        logger.info(TAG, "Traza iniciada: $nombre")
        return object : TraceHandle {
            override fun detener() {
                val ms = System.currentTimeMillis() - inicio
                logger.info(TAG, "Traza detenida: $nombre (${ms}ms)")
            }
        }
    }

    override fun setUserId(hashUsuario: String) {
        logger.info(TAG, "setUserId: $hashUsuario")
    }

    override fun setContexto(claves: Map<String, String>) {
        logger.info(TAG, "setContexto: $claves")
    }

    private companion object {
        const val TAG = "ConsoleTelemetry"
    }
}
