# ─── core:analytics consumer rules ───────────────────────────────────────────
# QUÉ NO SE OFUSCA y POR QUÉ:
#   AnalyticsEvent subtipos: el campo `nombre` se usa como nombre de evento en
#   Firebase Analytics (logEvent). Si se ofusca, los eventos llegan al dashboard
#   con nombres ilegibles y se pierde toda la trazabilidad de negocio.
#
#   ErrorRateLimiter y SessionIdProvider son clases internas de infraestructura;
#   no necesitan keep porque no se accede a ellas por reflexión ni por Firebase.
#   Solo se mantienen los nombres de eventos como strings de tiempo de ejecución.

-keepclassmembers class com.mango.fakestore.core.analytics.AnalyticsEvent$* {
    public static final java.lang.String nombre;
}
-keep enum com.mango.fakestore.core.analytics.** { *; }
