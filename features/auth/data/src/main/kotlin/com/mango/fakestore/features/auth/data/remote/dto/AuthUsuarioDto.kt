package com.mango.fakestore.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthUsuarioDto(
    val id: Int,
    val username: String,
    val email: String,
    val phone: String,
    val name: AuthNombreDto,
    val address: AuthDireccionDto,
)

@Serializable
data class AuthNombreDto(
    val firstname: String,
    val lastname: String,
)

@Serializable
data class AuthDireccionDto(
    val city: String,
    val street: String,
    val number: Int,
    val zipcode: String,
)
