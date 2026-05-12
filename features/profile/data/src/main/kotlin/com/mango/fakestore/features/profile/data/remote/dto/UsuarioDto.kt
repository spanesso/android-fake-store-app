package com.mango.fakestore.features.profile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioDto(
    val id: Int,
    val username: String,
    val email: String,
    val phone: String,
    val name: NombreDto,
    val address: DireccionDto,
)
