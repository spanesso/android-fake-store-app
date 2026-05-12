package com.mango.fakestore.features.products.data.mapper

import com.mango.fakestore.features.products.data.local.entity.ProductoEntity
import com.mango.fakestore.features.products.domain.model.Producto
import com.mango.fakestore.features.products.domain.model.Valoracion

fun ProductoEntity.toDomain(): Producto = Producto(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    precio = precio,
    categoria = categoria,
    imagenUrl = imagenUrl,
    valoracion = Valoracion(
        puntuacion = valoracionPuntuacion,
        numVotaciones = valoracionNumVotaciones,
    ),
)

fun Producto.toEntity(): ProductoEntity = ProductoEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    precio = precio,
    categoria = categoria,
    imagenUrl = imagenUrl,
    valoracionPuntuacion = valoracion.puntuacion,
    valoracionNumVotaciones = valoracion.numVotaciones,
)
