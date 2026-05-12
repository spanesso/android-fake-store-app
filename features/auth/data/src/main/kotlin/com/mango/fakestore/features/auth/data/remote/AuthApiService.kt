package com.mango.fakestore.features.auth.data.remote

import com.mango.fakestore.features.auth.data.remote.dto.AuthUsuarioDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AuthApiService {
    @GET("users/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Int): AuthUsuarioDto
}
