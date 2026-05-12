package com.mango.fakestore.features.products.data.mapper

import com.mango.fakestore.features.products.data.remote.dto.ProductoDto
import com.mango.fakestore.features.products.domain.model.Producto
import com.mango.fakestore.features.products.domain.model.Valoracion

fun ProductoDto.toDomain(): Producto = Producto(
    id = id,
    titulo = title,
    descripcion = description,
    precio = price,
    categoria = category,
    imagenUrl = image,
    valoracion = Valoracion(
        puntuacion = rating.rate,
        numVotaciones = rating.count,
    ),
)
