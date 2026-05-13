package com.mango.fakestore.features.auth.domain.usecase

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.auth.domain.model.SesionUsuario
import com.mango.fakestore.features.auth.domain.repository.SesionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SeleccionarUsuarioTest {

    private val repository: SesionRepository = mockk()
    private lateinit var seleccionarUsuario: SeleccionarUsuario

    @Before
    fun setUp() {
        seleccionarUsuario = SeleccionarUsuario(repository)
    }

    @Test
    fun `dado userId valido cuando se selecciona usuario entonces retorna SesionUsuario activa`() = runTest {
        val esperado = SesionUsuario(userId = 3, activa = true)
        coEvery { repository.seleccionarUsuario(3) } returns Either.Right(esperado)

        val resultado = seleccionarUsuario(3)

        assertThat(resultado).isInstanceOf(Either.Right::class.java)
        assertThat((resultado as Either.Right).value).isEqualTo(esperado)
        coVerify(exactly = 1) { repository.seleccionarUsuario(3) }
    }

    @Test
    fun `dado respuesta Network NotFound cuando se selecciona usuario entonces retorna Left NotFound`() = runTest {
        val error = DomainError.Network.NotFound()
        coEvery { repository.seleccionarUsuario(99) } returns Either.Left(error)

        val resultado = seleccionarUsuario(99)

        assertThat(resultado).isInstanceOf(Either.Left::class.java)
        assertThat((resultado as Either.Left).value).isInstanceOf(DomainError.Network.NotFound::class.java)
    }

    @Test
    fun `dado sin conexion cuando se selecciona usuario entonces retorna Left NoConnection`() = runTest {
        val error = DomainError.Network.NoConnection()
        coEvery { repository.seleccionarUsuario(any()) } returns Either.Left(error)

        val resultado = seleccionarUsuario(1)

        assertThat(resultado).isInstanceOf(Either.Left::class.java)
        assertThat((resultado as Either.Left).value).isInstanceOf(DomainError.Network.NoConnection::class.java)
    }
}
