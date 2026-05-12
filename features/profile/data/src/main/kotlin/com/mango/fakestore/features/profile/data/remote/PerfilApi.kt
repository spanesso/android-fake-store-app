package com.mango.fakestore.features.profile.data.remote

import com.mango.fakestore.features.profile.data.remote.dto.UsuarioDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PerfilApi {
    @GET("users/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Int): UsuarioDto
}
