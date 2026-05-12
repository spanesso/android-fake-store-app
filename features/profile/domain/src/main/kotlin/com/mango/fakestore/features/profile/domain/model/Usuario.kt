package com.mango.fakestore.features.profile.domain.model

data class Usuario(
    val id: Int,
    val nombreCompleto: String,
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val ciudad: String,
    val calle: String,
    val codigoPostal: String,
)
