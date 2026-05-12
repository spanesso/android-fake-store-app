# ─── features:products:data consumer rules ───────────────────────────────────
# QUÉ NO SE OFUSCA y POR QUÉ:
#   kotlinx.serialization KSP genera el $serializer companion en tiempo de
#   compilación. El runtime de serialización busca este companion por reflexión
#   para deserializar JSON. Los nombres de *clase* Kotlin no necesitan keep
#   (el acceso es por referencia directa), pero el *companion generado* sí.
#   Solo los @SerialName (= nombres de campo JSON) deben coincidir con la API;
#   los nombres de clase Kotlin pueden ofuscarse sin problema.

-keepclassmembers @kotlinx.serialization.Serializable class
    com.mango.fakestore.features.products.data.** {
    static ** $serializer;
    ** Companion;
}
