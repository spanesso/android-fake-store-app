package com.mango.fakestore.features.favorites.domain.usecase

import app.cash.turbine.test
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.favorites.domain.model.Favorito
import com.mango.fakestore.features.favorites.domain.repository.FavoritosRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObservarFavoritosTest {

    private lateinit var repositorio: FavoritosRepository
    private lateinit var observarFavoritos: ObservarFavoritos

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
        observarFavoritos = ObservarFavoritos(repositorio)
    }

    @Test
    fun `dado un repositorio cuando se invoca entonces delega al repositorio y retorna el mismo flow`() =
        runTest {
            val flowEsperado = flowOf(Either.Right(listOf(favoritoEjemplo)))
            every { repositorio.observarFavoritos() } returns flowEsperado

            val resultado = observarFavoritos()

            assertThat(resultado).isSameInstanceAs(flowEsperado)
            verify(exactly = 1) { repositorio.observarFavoritos() }
        }

    @Test
    fun `dado que el repositorio retorna lista de favoritos entonces el flow emite Either Right con la lista`() =
        runTest {
            val lista = listOf(favoritoEjemplo)
            every { repositorio.observarFavoritos() } returns flowOf(Either.Right(lista))

            observarFavoritos().test {
                val emision = awaitItem()
                assertThat(emision).isInstanceOf(Either.Right::class.java)
                assertThat((emision as Either.Right).value).isEqualTo(lista)
                awaitComplete()
            }
        }

    @Test
    fun `dado que el repositorio retorna lista vacía entonces el flow emite Either Right con lista vacía`() =
        runTest {
            every { repositorio.observarFavoritos() } returns flowOf(Either.Right(emptyList()))

            observarFavoritos().test {
                val emision = awaitItem()
                assertThat(emision).isInstanceOf(Either.Right::class.java)
                assertThat((emision as Either.Right).value).isEmpty()
                awaitComplete()
            }
        }

    @Test
    fun `dado que el repositorio retorna ReadFailed entonces el flow emite Either Left con ReadFailed`() =
        runTest {
            val error = DomainError.Database.ReadFailed()
            every { repositorio.observarFavoritos() } returns flowOf(Either.Left(error))

            observarFavoritos().test {
                val emision = awaitItem()
                assertThat(emision).isInstanceOf(Either.Left::class.java)
                assertThat((emision as Either.Left).value).isInstanceOf(DomainError.Database.ReadFailed::class.java)
                awaitComplete()
            }
        }
}
