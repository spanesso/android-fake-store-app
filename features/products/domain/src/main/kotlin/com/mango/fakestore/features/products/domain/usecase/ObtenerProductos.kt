package com.mango.fakestore.features.products.domain.usecase

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.products.domain.model.Producto
import com.mango.fakestore.features.products.domain.repository.ProductosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerProductos @Inject constructor(
    private val repositorio: ProductosRepository,
) {
    operator fun invoke(): Flow<Either<DomainError, List<Producto>>> =
        repositorio.obtenerProductos()
}
