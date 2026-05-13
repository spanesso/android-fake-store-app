package com.mango.fakestore.features.auth.domain.usecase

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.auth.domain.repository.SesionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CerrarSesionTest {

    private val repository: SesionRepository = mockk()
    private lateinit var cerrarSesion: CerrarSesion

    @Before
    fun setUp() {
        cerrarSesion = CerrarSesion(repository)
    }

    @Test
    fun `dado sesion activa cuando se cierra sesion entonces retorna Right Unit`() = runTest {
        coEvery { repository.cerrarSesion() } returns Either.Right(Unit)

        val resultado = cerrarSesion()

        assertThat(resultado).isInstanceOf(Either.Right::class.java)
        coVerify(exactly = 1) { repository.cerrarSesion() }
    }

    @Test
    fun `dado error de BD cuando se cierra sesion entonces retorna Left WriteFailed`() = runTest {
        val error = DomainError.Database.WriteFailed()
        coEvery { repository.cerrarSesion() } returns Either.Left(error)

        val resultado = cerrarSesion()

        assertThat(resultado).isInstanceOf(Either.Left::class.java)
        assertThat((resultado as Either.Left).value).isInstanceOf(DomainError.Database.WriteFailed::class.java)
    }
}
