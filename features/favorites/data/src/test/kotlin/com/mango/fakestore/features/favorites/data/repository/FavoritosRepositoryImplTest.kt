package com.mango.fakestore.features.favorites.data.repository

import app.cash.turbine.test
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.datastore.MangoDataStore
import com.mango.fakestore.core.datastore.model.SessionData
import com.mango.fakestore.core.testing.CoroutineTestRule
import com.mango.fakestore.features.favorites.data.local.FavoritosDao
import com.mango.fakestore.features.favorites.data.local.entity.FavoritoEntity
import com.mango.fakestore.features.favorites.domain.model.Favorito
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritosRepositoryImplTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val dao: FavoritosDao = mockk(relaxed = true)
    private val dataStore: MangoDataStore = mockk()
    private val sessionFlow = MutableStateFlow(SessionData(accessToken = null, refreshToken = null, userId = "1"))

    private lateinit var repositorio: FavoritosRepositoryImpl

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
        every { dataStore.sessionFlow } returns sessionFlow
        repositorio = FavoritosRepositoryImpl(dao, dataStore)
    }

    @Test
    fun `dado BD vacía cuando se observan favoritos entonces emite lista vacía`() = runTest {
        every { dao.observarFavoritos(1) } returns flowOf(emptyList())

        repositorio.observarFavoritos().test {
            val emision = awaitItem()
            assertThat(emision).isInstanceOf(Either.Right::class.java)
            assertThat((emision as Either.Right).value).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado favorito insertado cuando se observan favoritos entonces emite la lista con ese favorito`() = runTest {
        val entity = FavoritoEntity(
            productoId = 1,
            userId = 1,
            titulo = "Camiseta de lino",
            precio = 49.99,
            imagenUrl = "https://fakestoreapi.com/img/1.jpg",
            categoria = "ropa",
            fechaMarcado = 1_700_000_000_000L,
        )
        every { dao.observarFavoritos(1) } returns flowOf(listOf(entity))

        repositorio.observarFavoritos().test {
            val emision = awaitItem()
            assertThat(emision).isInstanceOf(Either.Right::class.java)
            val lista = (emision as Either.Right).value
            assertThat(lista).hasSize(1)
            assertThat(lista[0].productoId).isEqualTo(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado BD vacía cuando se marca un favorito nuevo entonces insertarFavorito es llamado`() = runTest {
        coEvery { dao.esFavorito(1, 1) } returns false
        coEvery { dao.insertarFavorito(any()) } returns Unit

        val resultado = repositorio.toggleFavorito(favoritoEjemplo)

        assertThat(resultado).isInstanceOf(Either.Right::class.java)
        coVerify { dao.insertarFavorito(any()) }
    }

    @Test
    fun `dado favorito existente cuando se hace toggle entonces borrarFavorito es llamado`() = runTest {
        coEvery { dao.esFavorito(1, 1) } returns true
        coEvery { dao.borrarFavorito(1, 1) } returns Unit

        val resultado = repositorio.toggleFavorito(favoritoEjemplo)

        assertThat(resultado).isInstanceOf(Either.Right::class.java)
        coVerify { dao.borrarFavorito(1, 1) }
    }

    @Test
    fun `dado BD vacía cuando se observa conteo entonces emite 0`() = runTest {
        every { dao.observarConteo(1) } returns flowOf(0)

        repositorio.observarConteo().test {
            assertThat(awaitItem()).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
