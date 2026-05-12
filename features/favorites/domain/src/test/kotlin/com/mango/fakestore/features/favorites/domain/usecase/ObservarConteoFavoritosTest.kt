package com.mango.fakestore.features.favorites.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.features.favorites.domain.repository.FavoritosRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObservarConteoFavoritosTest {

    private lateinit var repositorio: FavoritosRepository
    private lateinit var observarConteo: ObservarConteoFavoritos

    @Before
    fun setUp() {
        repositorio = mockk()
        observarConteo = ObservarConteoFavoritos(repositorio)
    }

    @Test
    fun `dado que no hay favoritos cuando se observa conteo entonces emite 0`() =
        runTest {
            every { repositorio.observarConteo() } returns flowOf(0)

            observarConteo().test {
                assertThat(awaitItem()).isEqualTo(0)
                awaitComplete()
            }
        }

    @Test
    fun `dado que hay N favoritos cuando se observa conteo entonces emite N`() =
        runTest {
            every { repositorio.observarConteo() } returns flowOf(3)

            observarConteo().test {
                assertThat(awaitItem()).isEqualTo(3)
                awaitComplete()
            }
        }
}
