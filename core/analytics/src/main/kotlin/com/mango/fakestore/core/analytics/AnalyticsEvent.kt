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

    // Eventos de negocio — ningún campo contiene PII
    data class ProductoVisto(val productoId: Int) : AnalyticsEvent {
        override val nombre = "product_viewed"
        override val params: Map<String, Any?> = mapOf("product_id" to productoId)
    }

    data class ProductoFavoritado(val productoId: Int) : AnalyticsEvent {
        override val nombre = "product_favorited"
        override val params: Map<String, Any?> = mapOf("product_id" to productoId)
    }

    data class ProductoDesfavoritado(val productoId: Int) : AnalyticsEvent {
        override val nombre = "product_unfavorited"
        override val params: Map<String, Any?> = mapOf("product_id" to productoId)
    }

    data object PerfilVisto : AnalyticsEvent {
        override val nombre = "profile_viewed"
        override val params: Map<String, Any?> = emptyMap()
    }

    data object LoginExitoso : AnalyticsEvent {
        override val nombre = "login_success"
        override val params: Map<String, Any?> = emptyMap()
    }

    // motivo: "cancelado" | "bloqueado" | "no_disponible" | "error_hw" — nunca datos biométricos
    data class LoginFallido(val motivo: String) : AnalyticsEvent {
        override val nombre = "login_failure"
        override val params: Map<String, Any?> = mapOf("motivo" to motivo)
    }
}
