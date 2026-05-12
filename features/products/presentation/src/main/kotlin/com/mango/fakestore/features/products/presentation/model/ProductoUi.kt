package com.mango.fakestore.features.products.presentation.model

data class ProductoUi(
    val id: Int,
    val titulo: String,
    val precio: String,
    val categoria: String,
    val imagenUrl: String,
    val puntuacion: Float,
    val numVotaciones: Int,
)
