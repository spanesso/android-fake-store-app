package com.mango.fakestore.features.auth.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.features.auth.domain.repository.SesionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObtenerSesionActivaTest {

    private val repository: SesionRepository = mockk()
    private lateinit var obtenerSesionActiva: ObtenerSesionActiva

    @Before
    fun setUp() {
        obtenerSesionActiva = ObtenerSesionActiva(repository)
    }

    @Test
    fun `dado sesion activa con userId cuando se observa entonces emite el userId`() = runTest {
        every { repository.obtenerSesionActiva() } returns flowOf(5)

        obtenerSesionActiva().test {
            assertThat(awaitItem()).isEqualTo(5)
            awaitComplete()
        }
    }

    @Test
    fun `dado sin sesion activa cuando se observa entonces emite null`() = runTest {
        every { repository.obtenerSesionActiva() } returns flowOf(null)

        obtenerSesionActiva().test {
            assertThat(awaitItem()).isNull()
            awaitComplete()
        }
    }
}
