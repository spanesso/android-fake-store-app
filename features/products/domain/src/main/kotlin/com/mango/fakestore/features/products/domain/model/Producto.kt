package com.mango.fakestore.features.products.domain.model

data class Producto(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val categoria: String,
    val imagenUrl: String,
    val valoracion: Valoracion,
)
