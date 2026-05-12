# ─── features:profile:data consumer rules ────────────────────────────────────
# QUÉ NO SE OFUSCA y POR QUÉ:
#   DTOs de usuario/perfil marcados con @Serializable. El razonamiento es el
#   mismo que en features:products:data — solo el $serializer companion generado
#   por KSP necesita keep; los nombres de clase pueden ofuscarse.

-keepclassmembers @kotlinx.serialization.Serializable class
    com.mango.fakestore.features.profile.data.** {
    static ** $serializer;
    ** Companion;
}
