package com.mango.fakestore.features.products.data.mapper

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.features.products.data.remote.dto.ProductoDto
import com.mango.fakestore.features.products.data.remote.dto.RatingDto
import org.junit.Test

class ProductoDtoMapperTest {

    private val dto = ProductoDto(
        id = 1,
        title = "Camiseta Blanca",
        description = "Camiseta de algodón premium",
        price = 29.99,
        category = "clothing",
        image = "https://fakestoreapi.com/img/1.jpg",
        rating = RatingDto(rate = 4.5, count = 120),
    )

    @Test
    fun `toDomain mapea id correctamente`() {
        val producto = dto.toDomain()
        assertThat(producto.id).isEqualTo(1)
    }

    @Test
    fun `toDomain mapea titulo desde title`() {
        val producto = dto.toDomain()
        assertThat(producto.titulo).isEqualTo("Camiseta Blanca")
    }

    @Test
    fun `toDomain mapea descripcion correctamente`() {
        val producto = dto.toDomain()
        assertThat(producto.descripcion).isEqualTo("Camiseta de algodón premium")
    }

    @Test
    fun `toDomain mapea precio correctamente`() {
        val producto = dto.toDomain()
        assertThat(producto.precio).isEqualTo(29.99)
    }

    @Test
    fun `toDomain mapea categoria correctamente`() {
        val producto = dto.toDomain()
        assertThat(producto.categoria).isEqualTo("clothing")
    }

    @Test
    fun `toDomain mapea imagenUrl desde image`() {
        val producto = dto.toDomain()
        assertThat(producto.imagenUrl).isEqualTo("https://fakestoreapi.com/img/1.jpg")
    }

    @Test
    fun `toDomain mapea puntuacion de valoracion`() {
        val producto = dto.toDomain()
        assertThat(producto.valoracion.puntuacion).isEqualTo(4.5)
    }

    @Test
    fun `toDomain mapea numVotaciones de valoracion`() {
        val producto = dto.toDomain()
        assertThat(producto.valoracion.numVotaciones).isEqualTo(120)
    }
}
