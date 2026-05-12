package com.mango.fakestore.features.favorites.domain.model

data class Favorito(
    val productoId: Int,
    val titulo: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val fechaMarcado: Long,
)
