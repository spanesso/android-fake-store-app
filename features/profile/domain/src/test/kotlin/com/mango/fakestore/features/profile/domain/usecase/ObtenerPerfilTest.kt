@file:Suppress("MaximumLineLength", "MaxLineLength")

package com.mango.fakestore.features.profile.domain.usecase

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.profile.domain.model.Usuario
import com.mango.fakestore.features.profile.domain.repository.PerfilRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObtenerPerfilTest {

    private lateinit var repositorio: PerfilRepository
    private lateinit var obtenerPerfil: ObtenerPerfil

    private val usuarioEjemplo = Usuario(
        id = 8,
        nombreCompleto = "John Doe",
        nombreUsuario = "johnd",
        email = "john@example.com",
        telefono = "1-570-236-7033",
        ciudad = "kilcoole",
        calle = "new road 7835",
        codigoPostal = "12926-3874",
    )

    @Before
    fun setUp() {
        repositorio = mockk()
        obtenerPerfil = ObtenerPerfil(repositorio)
    }

    @Test
    fun `dado un userId valido cuando el repositorio devuelve usuario entonces retorna Either Right con el usuario`() =
        runTest {
            coEvery { repositorio.obtenerPerfil(8) } returns Either.Right(usuarioEjemplo)

            val resultado = obtenerPerfil(8)

            assertThat(resultado).isInstanceOf(Either.Right::class.java)
            assertThat((resultado as Either.Right).value).isEqualTo(usuarioEjemplo)
            coVerify(exactly = 1) { repositorio.obtenerPerfil(8) }
        }

    @Test(expected = IllegalArgumentException::class)
    fun `dado un userId cero cuando se invoca entonces lanza IllegalArgumentException`() =
        runTest {
            obtenerPerfil(0)
        }

    @Test(expected = IllegalArgumentException::class)
    fun `dado un userId negativo cuando se invoca entonces lanza IllegalArgumentException`() =
        runTest {
            obtenerPerfil(-1)
        }

    @Test
    fun `dado un userId valido cuando el repositorio devuelve NotFound entonces propaga Either Left con NotFound`() =
        runTest {
            val error = DomainError.Network.NotFound()
            coEvery { repositorio.obtenerPerfil(8) } returns Either.Left(error)

            val resultado = obtenerPerfil(8)

            assertThat(resultado).isInstanceOf(Either.Left::class.java)
            assertThat((resultado as Either.Left).value).isInstanceOf(DomainError.Network.NotFound::class.java)
        }

    @Test
    fun `dado un userId valido cuando el repositorio devuelve NoConnection entonces propaga Either Left con NoConnection`() =
        runTest {
            coEvery { repositorio.obtenerPerfil(8) } returns Either.Left(DomainError.Network.NoConnection())

            val resultado = obtenerPerfil(8)

            assertThat((resultado as Either.Left).value).isInstanceOf(DomainError.Network.NoConnection::class.java)
        }

    @Test
    fun `dado un userId valido cuando el repositorio devuelve Timeout entonces propaga Either Left con Timeout`() =
        runTest {
            coEvery { repositorio.obtenerPerfil(8) } returns Either.Left(DomainError.Network.Timeout())

            val resultado = obtenerPerfil(8)

            assertThat((resultado as Either.Left).value).isInstanceOf(DomainError.Network.Timeout::class.java)
        }

    @Test
    fun `dado un userId valido cuando el repositorio devuelve Server 500 entonces propaga Either Left con Server`() =
        runTest {
            coEvery { repositorio.obtenerPerfil(8) } returns Either.Left(DomainError.Network.Server(500))

            val resultado = obtenerPerfil(8)

            val error = (resultado as Either.Left).value as DomainError.Network.Server
            assertThat(error.httpCode).isEqualTo(500)
        }

    @Test
    fun `dado un userId valido cuando el repositorio devuelve Parsing entonces propaga Either Left con Parsing`() =
        runTest {
            coEvery { repositorio.obtenerPerfil(8) } returns Either.Left(DomainError.Network.Parsing())

            val resultado = obtenerPerfil(8)

            assertThat((resultado as Either.Left).value).isInstanceOf(DomainError.Network.Parsing::class.java)
        }

    @Test
    fun `dado un userId valido cuando el repositorio devuelve Unknown entonces propaga Either Left con Unknown`() =
        runTest {
            coEvery { repositorio.obtenerPerfil(8) } returns Either.Left(DomainError.Unknown())

            val resultado = obtenerPerfil(8)

            assertThat((resultado as Either.Left).value).isInstanceOf(DomainError.Unknown::class.java)
        }
}
