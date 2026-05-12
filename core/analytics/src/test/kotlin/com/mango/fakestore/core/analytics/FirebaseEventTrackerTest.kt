package com.mango.fakestore.core.analytics

import com.mango.fakestore.core.analytics.impl.FirebaseEventTrackerImpl
import com.mango.fakestore.core.analytics.impl.NoOpTelemetryImpl
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class FirebaseEventTrackerTest {

    private val telemetry = mockk<Telemetry>(relaxed = true)
    private val tracker = FirebaseEventTrackerImpl(telemetry)

    @Test
    fun `registrar PantallaVista llama a telemetry con nombre screen_view`() {
        tracker.registrar(AnalyticsEvent.PantallaVista("productos"))
        verify { telemetry.registrarEvento("screen_view", match { it["screen_name"] == "productos" }) }
    }

    @Test
    fun `registrar AccionUsuario llama a telemetry con nombre correcto`() {
        tracker.registrar(AnalyticsEvent.AccionUsuario("agregar_favorito"))
        verify { telemetry.registrarEvento("agregar_favorito", any()) }
    }

    @Test
    fun `registrar ErrorRegistrado llama a telemetry con codigo y detalle`() {
        tracker.registrar(AnalyticsEvent.ErrorRegistrado("NET-001", "Sin conexion"))
        verify { telemetry.registrarEvento("error_registrado", match { it["codigo"] == "NET-001" }) }
    }

    @Test
    fun `NoOpEventTracker registrar no lanza excepcion`() {
        val noOp = com.mango.fakestore.core.analytics.impl.NoOpEventTrackerImpl()
        noOp.registrar(AnalyticsEvent.PantallaVista("inicio"))
    }
}
