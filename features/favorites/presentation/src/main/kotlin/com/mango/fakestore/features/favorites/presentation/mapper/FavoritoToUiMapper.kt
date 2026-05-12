package com.mango.fakestore.features.favorites.presentation.mapper

import com.mango.fakestore.features.favorites.domain.model.Favorito
import com.mango.fakestore.features.favorites.presentation.model.FavoritoUi

fun Favorito.toUi(): FavoritoUi = FavoritoUi(
    productoId = productoId,
    titulo = titulo,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
)
