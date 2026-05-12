package com.mango.fakestore.features.favorites.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.features.favorites.data.local.FavoritosDao
import com.mango.fakestore.features.favorites.data.local.entity.FavoritoEntity
import com.mango.fakestore.features.favorites.domain.model.Favorito
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FavoritosRepositoryImplTest {

    private lateinit var db: androidx.room.RoomDatabase
    private lateinit var dao: FavoritosDao
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
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            FavoritosTestDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = (db as FavoritosTestDatabase).favoritosDao()
        repositorio = FavoritosRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `dado BD vacía cuando se observan favoritos entonces emite lista vacía`() = runTest {
        repositorio.observarFavoritos().test {
            val emision = awaitItem()
            assertThat(emision).isInstanceOf(Either.Right::class.java)
            assertThat((emision as Either.Right).value).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado favorito insertado cuando se observan favoritos entonces emite la lista con ese favorito`() =
        runTest {
            dao.insertarFavorito(
                FavoritoEntity(
                    productoId = 1,
                    titulo = "Camiseta de lino",
                    precio = 49.99,
                    imagenUrl = "https://fakestoreapi.com/img/1.jpg",
                    categoria = "ropa",
                    fechaMarcado = 1_700_000_000_000L,
                ),
            )

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
    fun `dado BD vacía cuando se marca un favorito nuevo entonces esFavorito retorna true`() =
        runTest {
            val resultado = repositorio.toggleFavorito(favoritoEjemplo)

            assertThat(resultado).isInstanceOf(Either.Right::class.java)
            assertThat(dao.esFavorito(1)).isTrue()
        }

    @Test
    fun `dado favorito existente cuando se hace toggle entonces se borra y esFavorito retorna false`() =
        runTest {
            dao.insertarFavorito(
                FavoritoEntity(
                    productoId = 1,
                    titulo = "Camiseta",
                    precio = 49.99,
                    imagenUrl = "",
                    categoria = "ropa",
                    fechaMarcado = 0L,
                ),
            )

            val resultado = repositorio.toggleFavorito(favoritoEjemplo)

            assertThat(resultado).isInstanceOf(Either.Right::class.java)
            assertThat(dao.esFavorito(1)).isFalse()
        }

    @Test
    fun `dado BD vacía cuando se observa conteo entonces emite 0`() = runTest {
        repositorio.observarConteo().test {
            assertThat(awaitItem()).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
