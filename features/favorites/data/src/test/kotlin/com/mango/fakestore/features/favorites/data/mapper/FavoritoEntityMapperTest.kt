package com.mango.fakestore.features.favorites.data.mapper

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.features.favorites.data.local.entity.FavoritoEntity
import com.mango.fakestore.features.favorites.domain.model.Favorito
import org.junit.Test

class FavoritoEntityMapperTest {

    private val entity = FavoritoEntity(
        productoId = 1,
        titulo = "Camiseta de lino",
        precio = 49.99,
        imagenUrl = "https://fakestoreapi.com/img/1.jpg",
        categoria = "ropa",
        fechaMarcado = 1_700_000_000_000L,
    )

    private val domain = Favorito(
        productoId = 1,
        titulo = "Camiseta de lino",
        precio = 49.99,
        imagenUrl = "https://fakestoreapi.com/img/1.jpg",
        categoria = "ropa",
        fechaMarcado = 1_700_000_000_000L,
    )

    @Test
    fun `dado FavoritoEntity cuando se mapea a domain entonces todos los campos son correctos`() {
        val resultado = entity.toDomain()

        assertThat(resultado.productoId).isEqualTo(entity.productoId)
        assertThat(resultado.titulo).isEqualTo(entity.titulo)
        assertThat(resultado.precio).isEqualTo(entity.precio)
        assertThat(resultado.imagenUrl).isEqualTo(entity.imagenUrl)
        assertThat(resultado.categoria).isEqualTo(entity.categoria)
        assertThat(resultado.fechaMarcado).isEqualTo(entity.fechaMarcado)
    }

    @Test
    fun `dado Favorito domain cuando se mapea a entity entonces todos los campos son correctos`() {
        val resultado = domain.toEntity()

        assertThat(resultado.productoId).isEqualTo(domain.productoId)
        assertThat(resultado.titulo).isEqualTo(domain.titulo)
        assertThat(resultado.precio).isEqualTo(domain.precio)
        assertThat(resultado.imagenUrl).isEqualTo(domain.imagenUrl)
        assertThat(resultado.categoria).isEqualTo(domain.categoria)
        assertThat(resultado.fechaMarcado).isEqualTo(domain.fechaMarcado)
    }
}
