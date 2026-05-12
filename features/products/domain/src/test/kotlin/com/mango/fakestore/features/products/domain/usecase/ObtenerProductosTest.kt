package com.mango.fakestore.features.products.domain.usecase

import app.cash.turbine.test
import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.products.domain.model.Producto
import com.mango.fakestore.features.products.domain.model.Valoracion
import com.mango.fakestore.features.products.domain.repository.ProductosRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObtenerProductosTest {

    private lateinit var repositorio: ProductosRepository
    private lateinit var obtenerProductos: ObtenerProductos

    private val productoEjemplo = Producto(
        id = 1,
        titulo = "Camiseta de prueba",
        descripcion = "Descripción de prueba",
        precio = 29.99,
        categoria = "ropa",
        imagenUrl = "https://fakestoreapi.com/img/1.jpg",
        valoracion = Valoracion(puntuacion = 4.5, numVotaciones = 120),
    )

    @Before
    fun setUp() {
        repositorio = mockk()
        obtenerProductos = ObtenerProductos(repositorio)
    }

    @Test
    fun `dado un repositorio cuando se invoca el caso de uso entonces delega al repositorio y retorna el mismo flow`() =
        runTest {
            val flowEsperado = flowOf(Either.Right(listOf(productoEjemplo)))
            every { repositorio.obtenerProductos() } returns flowEsperado

            val resultado = obtenerProductos()

            assertThat(resultado).isSameInstanceAs(flowEsperado)
            verify(exactly = 1) { repositorio.obtenerProductos() }
        }

    @Test
    fun `dado que el repositorio retorna una lista de productos cuando se invoca el caso de uso entonces el flow emite Either Right con la lista`() =
        runTest {
            val lista = listOf(productoEjemplo)
            every { repositorio.obtenerProductos() } returns flowOf(Either.Right(lista))

            obtenerProductos().test {
                val emision = awaitItem()
                assertThat(emision).isInstanceOf(Either.Right::class.java)
                assertThat((emision as Either.Right).value).isEqualTo(lista)
                awaitComplete()
            }
        }

    @Test
    fun `dado que el repositorio retorna una lista vacía cuando se invoca el caso de uso entonces el flow emite Either Right con lista vacía`() =
        runTest {
            every { repositorio.obtenerProductos() } returns flowOf(Either.Right(emptyList()))

            obtenerProductos().test {
                val emision = awaitItem()
                assertThat(emision).isInstanceOf(Either.Right::class.java)
                assertThat((emision as Either.Right).value).isEmpty()
                awaitComplete()
            }
        }

    @Test
    fun `dado que el repositorio retorna NoConnection cuando se invoca el caso de uso entonces el flow emite Either Left con NoConnection`() =
        runTest {
            val error = DomainError.Network.NoConnection()
            every { repositorio.obtenerProductos() } returns flowOf(Either.Left(error))

            obtenerProductos().test {
                val emision = awaitItem()
                assertThat(emision).isInstanceOf(Either.Left::class.java)
                assertThat((emision as Either.Left).value).isInstanceOf(DomainError.Network.NoConnection::class.java)
                awaitComplete()
            }
        }

    @Test
    fun `dado que el repositorio retorna Timeout cuando se invoca el caso de uso entonces el flow emite Either Left con Timeout`() =
        runTest {
            val error = DomainError.Network.Timeout()
            every { repositorio.obtenerProductos() } returns flowOf(Either.Left(error))

            obtenerProductos().test {
                val emision = awaitItem()
                assertThat(emision).isInstanceOf(Either.Left::class.java)
                assertThat((emision as Either.Left).value).isInstanceOf(DomainError.Network.Timeout::class.java)
                awaitComplete()
            }
        }

    @Test
    fun `dado que el repositorio retorna Server 500 cuando se invoca el caso de uso entonces el flow emite Either Left con Server`() =
        runTest {
            val error = DomainError.Network.Server(httpCode = 500)
            every { repositorio.obtenerProductos() } returns flowOf(Either.Left(error))

            obtenerProductos().test {
                val emision = awaitItem()
                assertThat(emision).isInstanceOf(Either.Left::class.java)
                val domainError = (emision as Either.Left).value
                assertThat(domainError).isInstanceOf(DomainError.Network.Server::class.java)
                assertThat((domainError as DomainError.Network.Server).httpCode).isEqualTo(500)
                awaitComplete()
            }
        }

    @Test
    fun `dado que el repositorio retorna ReadFailed cuando se invoca el caso de uso entonces el flow emite Either Left con ReadFailed`() =
        runTest {
            val error = DomainError.Database.ReadFailed()
            every { repositorio.obtenerProductos() } returns flowOf(Either.Left(error))

            obtenerProductos().test {
                val emision = awaitItem()
                assertThat(emision).isInstanceOf(Either.Left::class.java)
                assertThat((emision as Either.Left).value).isInstanceOf(DomainError.Database.ReadFailed::class.java)
                awaitComplete()
            }
        }
}
