package com.mango.fakestore.features.products.domain.repository

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.products.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductosRepository {
    fun obtenerProductos(): Flow<Either<DomainError, List<Producto>>>
}
