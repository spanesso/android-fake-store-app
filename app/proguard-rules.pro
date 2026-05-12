# ─── Reglas agresivas de R8 — app Mango Fake Store ───────────────────────────
# Aplicadas solo en buildTypes release (isMinifyEnabled = true).
# Cada sección explica QUÉ se ofusca y QUÉ se preserva y POR QUÉ.

# Reempaquetar todas las clases en un paquete plano (anti-ingeniería inversa).
-repackageclasses 'o'

# Ampliar visibilidad para optimización cross-class (permite inlining).
-allowaccessmodification

# Sobrecargar métodos agresivamente para dificultar análisis de flujo.
-overloadaggressively

# 5 pasadas de optimización (más agresivo que el default de 1).
-optimizationpasses 5

# Preservar información de línea para stack traces de Crashlytics en producción.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preservar anotaciones y generics requeridos por frameworks de inyección y serialización.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# ─── Logs — eliminar en release ───────────────────────────────────────────────
# android.util.Log y Timber no deben existir en la APK de producción (no PII).
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
-assumenosideeffects class timber.log.Timber {
    public static void d(...);
    public static void v(...);
    public static void i(...);
}

# ─── Kotlin ──────────────────────────────────────────────────────────────────
# Kotlin Metadata es requerido por la reflexión de Kotlin y por algunas
# bibliotecas (Retrofit, kotlinx.serialization) para introspección de tipos.
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# ─── Hilt / Dagger ───────────────────────────────────────────────────────────
# Hilt genera fábricas de componentes en tiempo de compilación que referencian
# los módulos @InstallIn y @Module por nombre. Si R8 los renombra, las fábricas
# generadas no los encuentran en runtime.
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.Module class * { *; }
-keepclassmembers @dagger.hilt.InstallIn class * { *; }
-keepclassmembers @dagger.Module class * { *; }
-dontwarn dagger.**

# ─── Jetpack Compose ─────────────────────────────────────────────────────────
# El compilador de Compose genera clases auxiliares (ComposableSingletons,
# ComposerKt) con nombres específicos. R8 no debe reempaquetarlas porque el
# runtime de Compose las busca por nombre de clase.
-keep class androidx.compose.runtime.** { *; }
-keep class **ComposableSingletons* { *; }
-dontwarn androidx.compose.**

# ─── kotlinx.serialization ───────────────────────────────────────────────────
# KSP genera serializers en compilación. Los nombres de clase no necesitan keep,
# pero el companion $serializer SÍ, porque el runtime lo busca por reflexión.
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    static ** $serializer;
    ** Companion;
}
-keepattributes *Annotation*
-dontwarn kotlinx.serialization.**

# ─── Arrow Core ──────────────────────────────────────────────────────────────
# Either y sus subclases se usan en contratos de dominio. Si se ofuscan,
# el pattern matching (when) y las extensiones dejarían de funcionar.
-keep class arrow.core.Either { *; }
-keep class arrow.core.Either$* { *; }
-dontwarn arrow.**

# ─── RootBeer ────────────────────────────────────────────────────────────────
# RootBeer accede a comandos del sistema y rutas de fichero por nombre. Si sus
# clases se ofuscan, las detecciones de root fallan silenciosamente.
-keep class com.scottyab.rootbeer.** { *; }

# ─── Room ────────────────────────────────────────────────────────────────────
# Las keep rules de Room se gestionan en core/database/consumer-rules.pro.

# ─── Retrofit ────────────────────────────────────────────────────────────────
# Las keep rules de Retrofit se gestionan en core/network/consumer-rules.pro.

# ─── Warnings silenciados ────────────────────────────────────────────────────
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.**
