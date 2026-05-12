package com.mango.fakestore.features.favorites.data.mapper

import com.mango.fakestore.features.favorites.data.local.entity.FavoritoEntity
import com.mango.fakestore.features.favorites.domain.model.Favorito

fun FavoritoEntity.toDomain(): Favorito = Favorito(
    productoId = productoId,
    titulo = titulo,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
    fechaMarcado = fechaMarcado,
)

fun Favorito.toEntity(): FavoritoEntity = FavoritoEntity(
    productoId = productoId,
    titulo = titulo,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
    fechaMarcado = fechaMarcado,
)
