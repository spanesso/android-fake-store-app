package com.mango.fakestore.core.analytics

import com.mango.fakestore.core.analytics.impl.NoOpTelemetryImpl
import com.mango.fakestore.core.error.DomainError
import org.junit.Test

class NoOpTelemetryTest {

    private val telemetry = NoOpTelemetryImpl()

    @Test
    fun `reportarNoFatal no lanza excepcion`() {
        telemetry.reportarNoFatal(DomainError.Network.NoConnection())
    }

    @Test
    fun `registrarEvento no lanza excepcion`() {
        telemetry.registrarEvento("evento_test", mapOf("clave" to "valor"))
    }

    @Test
    fun `iniciarTraza retorna handle que se puede detener sin excepcion`() {
        val handle = telemetry.iniciarTraza("traza_test")
        handle.detener()
    }

    @Test
    fun `reportarNoFatal con contexto no lanza excepcion`() {
        val contexto = mapOf("modulo" to "products", "accion" to "cargar")
        telemetry.reportarNoFatal(DomainError.Network.Timeout(), contexto)
    }

    @Test
    fun `reportarNoFatal con error Unknown no lanza excepcion`() {
        telemetry.reportarNoFatal(DomainError.Unknown(RuntimeException("test")))
    }
}
