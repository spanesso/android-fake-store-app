package com.mango.fakestore.features.products.data.mapper

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.features.products.data.local.entity.ProductoEntity
import com.mango.fakestore.features.products.domain.model.Producto
import com.mango.fakestore.features.products.domain.model.Valoracion
import org.junit.Test

class ProductoEntityMapperTest {

    private val entity = ProductoEntity(
        id = 2,
        titulo = "Pantalón Slim",
        descripcion = "Pantalón de corte slim",
        precio = 59.99,
        categoria = "clothing",
        imagenUrl = "https://fakestoreapi.com/img/2.jpg",
        valoracionPuntuacion = 3.8,
        valoracionNumVotaciones = 200,
    )

    private val domainModel = Producto(
        id = 2,
        titulo = "Pantalón Slim",
        descripcion = "Pantalón de corte slim",
        precio = 59.99,
        categoria = "clothing",
        imagenUrl = "https://fakestoreapi.com/img/2.jpg",
        valoracion = Valoracion(puntuacion = 3.8, numVotaciones = 200),
    )

    @Test
    fun `toDomain desde entity mapea id correctamente`() {
        assertThat(entity.toDomain().id).isEqualTo(2)
    }

    @Test
    fun `toDomain desde entity mapea titulo correctamente`() {
        assertThat(entity.toDomain().titulo).isEqualTo("Pantalón Slim")
    }

    @Test
    fun `toDomain desde entity mapea descripcion correctamente`() {
        assertThat(entity.toDomain().descripcion).isEqualTo("Pantalón de corte slim")
    }

    @Test
    fun `toDomain desde entity mapea precio correctamente`() {
        assertThat(entity.toDomain().precio).isEqualTo(59.99)
    }

    @Test
    fun `toDomain desde entity mapea categoria correctamente`() {
        assertThat(entity.toDomain().categoria).isEqualTo("clothing")
    }

    @Test
    fun `toDomain desde entity mapea imagenUrl correctamente`() {
        assertThat(entity.toDomain().imagenUrl).isEqualTo("https://fakestoreapi.com/img/2.jpg")
    }

    @Test
    fun `toDomain desde entity mapea puntuacion de valoracion`() {
        assertThat(entity.toDomain().valoracion.puntuacion).isEqualTo(3.8)
    }

    @Test
    fun `toDomain desde entity mapea numVotaciones de valoracion`() {
        assertThat(entity.toDomain().valoracion.numVotaciones).isEqualTo(200)
    }

    @Test
    fun `toEntity mapea id correctamente`() {
        assertThat(domainModel.toEntity().id).isEqualTo(2)
    }

    @Test
    fun `toEntity mapea titulo correctamente`() {
        assertThat(domainModel.toEntity().titulo).isEqualTo("Pantalón Slim")
    }

    @Test
    fun `toEntity mapea descripcion correctamente`() {
        assertThat(domainModel.toEntity().descripcion).isEqualTo("Pantalón de corte slim")
    }

    @Test
    fun `toEntity mapea precio correctamente`() {
        assertThat(domainModel.toEntity().precio).isEqualTo(59.99)
    }

    @Test
    fun `toEntity mapea categoria correctamente`() {
        assertThat(domainModel.toEntity().categoria).isEqualTo("clothing")
    }

    @Test
    fun `toEntity mapea imagenUrl correctamente`() {
        assertThat(domainModel.toEntity().imagenUrl).isEqualTo("https://fakestoreapi.com/img/2.jpg")
    }

    @Test
    fun `toEntity mapea valoracionPuntuacion correctamente`() {
        assertThat(domainModel.toEntity().valoracionPuntuacion).isEqualTo(3.8)
    }

    @Test
    fun `toEntity mapea valoracionNumVotaciones correctamente`() {
        assertThat(domainModel.toEntity().valoracionNumVotaciones).isEqualTo(200)
    }

    @Test
    fun `roundtrip entity a domain y de vuelta a entity preserva todos los campos`() {
        val roundtrip = entity.toDomain().toEntity()
        assertThat(roundtrip.id).isEqualTo(entity.id)
        assertThat(roundtrip.titulo).isEqualTo(entity.titulo)
        assertThat(roundtrip.precio).isEqualTo(entity.precio)
        assertThat(roundtrip.valoracionPuntuacion).isEqualTo(entity.valoracionPuntuacion)
    }
}
