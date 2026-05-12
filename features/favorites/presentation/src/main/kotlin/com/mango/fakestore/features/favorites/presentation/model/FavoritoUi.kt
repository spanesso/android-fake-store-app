package com.mango.fakestore.features.favorites.presentation.model

data class FavoritoUi(
    val productoId: Int,
    val titulo: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
)
