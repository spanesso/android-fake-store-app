package com.mango.fakestore.core.analytics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AnalyticsEventTest {

    @Test
    fun `PantallaVista tiene nombre screen_view y param screen_name`() {
        val evento = AnalyticsEvent.PantallaVista("login")
        assertThat(evento.nombre).isEqualTo("screen_view")
        assertThat(evento.params["screen_name"]).isEqualTo("login")
    }

    @Test
    fun `AccionUsuario propaga nombre y params`() {
        val evento = AnalyticsEvent.AccionUsuario("click_boton", mapOf("boton" to "comprar"))
        assertThat(evento.nombre).isEqualTo("click_boton")
        assertThat(evento.params["boton"]).isEqualTo("comprar")
    }

    @Test
    fun `ErrorRegistrado tiene nombre error_registrado con codigo y detalle`() {
        val evento = AnalyticsEvent.ErrorRegistrado("DB-002", "lectura fallida")
        assertThat(evento.nombre).isEqualTo("error_registrado")
        assertThat(evento.params["codigo"]).isEqualTo("DB-002")
        assertThat(evento.params["detalle"]).isEqualTo("lectura fallida")
    }

    @Test
    fun `BusquedaRealizada tiene nombre busqueda_realizada con consulta y resultados`() {
        val evento = AnalyticsEvent.BusquedaRealizada("camiseta", 42)
        assertThat(evento.nombre).isEqualTo("busqueda_realizada")
        assertThat(evento.params["consulta"]).isEqualTo("camiseta")
        assertThat(evento.params["resultados"]).isEqualTo(42)
    }
}
