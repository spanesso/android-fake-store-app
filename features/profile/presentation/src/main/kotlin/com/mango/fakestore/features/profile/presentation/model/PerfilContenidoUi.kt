package com.mango.fakestore.features.profile.presentation.model

data class PerfilContenidoUi(
    val id: Int,
    val nombreCompleto: String,
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val ciudad: String,
    val calle: String,
    val codigoPostal: String,
    val contadorFavoritos: Int = 0,
)
