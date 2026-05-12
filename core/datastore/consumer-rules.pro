# ─── core:datastore consumer rules ───────────────────────────────────────────
# QUÉ NO SE OFUSCA y POR QUÉ:
#   Implementaciones de androidx.datastore.core.Serializer<T> — DataStore puede
#   instanciarlas mediante reflexión durante la inicialización. Si la clase se
#   reempaqueta o renombra, la instanciación falla con ClassNotFoundException.

-keep class * implements androidx.datastore.core.Serializer { *; }
-keepclassmembers class * implements androidx.datastore.core.Serializer { *; }

-dontwarn androidx.datastore.**
