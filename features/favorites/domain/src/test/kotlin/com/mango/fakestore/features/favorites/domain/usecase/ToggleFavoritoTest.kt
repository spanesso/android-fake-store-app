package com.mango.fakestore.features.favorites.domain.usecase

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.favorites.domain.model.Favorito
import com.mango.fakestore.features.favorites.domain.repository.FavoritosRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleFavoritoTest {

    private lateinit var repositorio: FavoritosRepository
    private lateinit var toggleFavorito: ToggleFavorito

    private val favoritoEjemplo = Favorito(
        productoId = 1,
        titulo = "Camiseta de lino",
        precio = 49.99,
        imagenUrl = "https://fakestoreapi.com/img/1.jpg",
        categoria = "ropa",
        fechaMarcado = 1_700_000_000_000L,
    )

    @Before
    fun setUp() {
        repositorio = mockk()
        toggleFavorito = ToggleFavorito(repositorio)
    }

    @Test
    fun `dado favorito nuevo cuando se hace toggle entonces delega al repositorio y retorna Right Unit`() =
        runTest {
            coEvery { repositorio.toggleFavorito(favoritoEjemplo) } returns Either.Right(Unit)

            val resultado = toggleFavorito(favoritoEjemplo)

            assertThat(resultado).isInstanceOf(Either.Right::class.java)
            assertThat((resultado as Either.Right).value).isEqualTo(Unit)
            coVerify(exactly = 1) { repositorio.toggleFavorito(favoritoEjemplo) }
        }

    @Test
    fun `dado favorito existente cuando se hace toggle entonces delega al repositorio correctamente`() =
        runTest {
            coEvery { repositorio.toggleFavorito(favoritoEjemplo) } returns Either.Right(Unit)

            val resultado = toggleFavorito(favoritoEjemplo)

            assertThat(resultado).isInstanceOf(Either.Right::class.java)
        }

    @Test
    fun `dado fallo de escritura en BD cuando se hace toggle entonces retorna Left WriteFailed`() =
        runTest {
            val error = DomainError.Database.WriteFailed()
            coEvery { repositorio.toggleFavorito(favoritoEjemplo) } returns Either.Left(error)

            val resultado = toggleFavorito(favoritoEjemplo)

            assertThat(resultado).isInstanceOf(Either.Left::class.java)
            assertThat((resultado as Either.Left).value).isInstanceOf(DomainError.Database.WriteFailed::class.java)
        }
}
