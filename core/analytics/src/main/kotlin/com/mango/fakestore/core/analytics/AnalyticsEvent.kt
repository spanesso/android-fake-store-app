package com.mango.fakestore.core.analytics

sealed interface AnalyticsEvent {
    val nombre: String
    val params: Map<String, Any?>

    data class PantallaVista(val pantalla: String) : AnalyticsEvent {
        override val nombre = "screen_view"
        override val params = mapOf("screen_name" to pantalla)
    }

    data class AccionUsuario(
        override val nombre: String,
        override val params: Map<String, Any?> = emptyMap(),
    ) : AnalyticsEvent

    data class ErrorRegistrado(val codigo: String, val detalle: String) : AnalyticsEvent {
        override val nombre = "error_registrado"
        override val params: Map<String, Any?> = mapOf("codigo" to codigo, "detalle" to detalle)
    }

    data class BusquedaRealizada(val consulta: String, val resultados: Int) : AnalyticsEvent {
        override val nombre = "busqueda_realizada"
        override val params: Map<String, Any?> = mapOf("consulta" to consulta, "resultados" to resultados)
    }
}
