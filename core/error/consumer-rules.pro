# ─── core:error consumer rules ───────────────────────────────────────────────
# QUÉ NO SE OFUSCA y POR QUÉ:
#   DomainError y sus subclases — Crashlytics clasifica errores por simpleName.
#   Sin nombres legibles, los dashboards de monitorización agrupan todos los
#   errores bajo el mismo nombre ofuscado y se pierde la observabilidad.

-keep class com.mango.fakestore.core.error.DomainError { *; }
-keep class com.mango.fakestore.core.error.DomainError$* { *; }
-keep class com.mango.fakestore.core.error.UiError { *; }

# UiError.messageRes es accedido por stringResource() en Composables.
-keepclassmembers class com.mango.fakestore.core.error.UiError {
    public int messageRes;
}
