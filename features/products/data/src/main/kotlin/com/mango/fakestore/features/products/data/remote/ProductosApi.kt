package com.mango.fakestore.features.products.data.remote

import com.mango.fakestore.features.products.data.remote.dto.ProductoDto
import retrofit2.http.GET

@Suppress("FunctionalInterface")
interface ProductosApi {
    @GET("products")
    suspend fun obtenerProductos(): List<ProductoDto>
}
