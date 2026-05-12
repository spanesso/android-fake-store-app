# ─── features:favorites:data consumer rules ──────────────────────────────────
# QUÉ NO SE OFUSCA y POR QUÉ:
#   Mismo razonamiento que features:products:data para kotlinx.serialization.
#   Además FavoritoEntity está anotada con @Entity (Room); su nombre de tabla
#   y sus campos deben ser estables para que las migraciones funcionen.
#   Las reglas de Room se aplican transitivamente desde core:database,
#   pero se repiten aquí para claridad del módulo.

-keepclassmembers @kotlinx.serialization.Serializable class
    com.mango.fakestore.features.favorites.data.** {
    static ** $serializer;
    ** Companion;
}

-keep @androidx.room.Entity class
    com.mango.fakestore.features.favorites.data.** { *; }
