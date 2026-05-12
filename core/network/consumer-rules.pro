# ─── core:network consumer rules ─────────────────────────────────────────────
# QUÉ NO SE OFUSCA y POR QUÉ:
#   Interfaces Retrofit anotadas con @GET, @POST, @PUT, @DELETE, @Query, etc.
#   Retrofit genera un Proxy Java en runtime que busca los métodos por nombre
#   y anotación mediante reflexión. Si R8 renombra los métodos, el proxy no
#   los encuentra y se producen errores en tiempo de ejecución.

-keep interface com.mango.fakestore.core.network.** { *; }
-keepclassmembers interface * {
    @retrofit2.http.GET <methods>;
    @retrofit2.http.POST <methods>;
    @retrofit2.http.PUT <methods>;
    @retrofit2.http.DELETE <methods>;
    @retrofit2.http.PATCH <methods>;
    @retrofit2.http.HEAD <methods>;
    @retrofit2.http.OPTIONS <methods>;
    @retrofit2.http.HTTP <methods>;
}

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn javax.annotation.**
