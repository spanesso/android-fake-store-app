package com.mango.fakestore.features.products.presentation.mapper

import com.mango.fakestore.features.products.domain.model.Producto
import com.mango.fakestore.features.products.presentation.model.ProductoUi

fun Producto.toUi(esFavorito: Boolean = false): ProductoUi = ProductoUi(
    id = id,
    titulo = titulo,
    precio = "$${"%.2f".format(precio)}",
    precioDouble = precio,
    categoria = categoria,
    imagenUrl = imagenUrl,
    puntuacion = valoracion.puntuacion.toFloat(),
    numVotaciones = valoracion.numVotaciones,
    esFavorito = esFavorito,
)
