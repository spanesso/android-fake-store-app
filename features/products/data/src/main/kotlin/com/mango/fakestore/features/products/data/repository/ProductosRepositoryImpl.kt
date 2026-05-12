package com.mango.fakestore.features.products.data.repository

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.ext.safeApiCall
import com.mango.fakestore.core.error.ext.safeDbCall
import com.mango.fakestore.features.products.data.local.ProductosDao
import com.mango.fakestore.features.products.data.mapper.toDomain
import com.mango.fakestore.features.products.data.mapper.toEntity
import com.mango.fakestore.features.products.data.remote.ProductosApi
import com.mango.fakestore.features.products.domain.model.Producto
import com.mango.fakestore.features.products.domain.repository.ProductosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductosRepositoryImpl @Inject constructor(
    private val api: ProductosApi,
    private val dao: ProductosDao,
) : ProductosRepository {

    override fun obtenerProductos(): Flow<Either<DomainError, List<Producto>>> = flow {
        // 1. Emitir datos en caché primero si están disponibles
        val datosLocales = dao.observarProductos().first()
        if (datosLocales.isNotEmpty()) {
            emit(Either.Right(datosLocales.map { it.toDomain() }))
        }

        // 2. Intentar actualizar desde la red
        val resultadoRed = safeApiCall { api.obtenerProductos() }

        resultadoRed.fold(
            ifLeft = { error ->
                // Si no hay caché, propagar el error
                if (datosLocales.isEmpty()) {
                    emit(Either.Left(error))
                }
            },
            ifRight = { dtos ->
                if (dtos.isEmpty()) {
                    safeDbCall { dao.borrarTodos() }
                    emit(Either.Right(emptyList()))
                    return@fold
                }

                // 3. Actualizar caché con datos frescos
                safeDbCall {
                    dao.borrarTodos()
                    dao.insertarProductos(dtos.map { it.toDomain().toEntity() })
                }.fold(
                    ifLeft = { dbError -> emit(Either.Left(dbError)) },
                    ifRight = {
                        val datosActualizados = dao.observarProductos().first()
                        emit(Either.Right(datosActualizados.map { it.toDomain() }))
                    },
                )
            },
        )
    }
}
