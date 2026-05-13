package com.mango.fakestore.features.auth.data.repository

import app.cash.turbine.test
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.datastore.MangoDataStore
import com.mango.fakestore.core.datastore.model.SessionData
import com.mango.fakestore.core.testing.CoroutineTestRule
import com.mango.fakestore.features.auth.data.remote.AuthApiService
import com.mango.fakestore.features.auth.data.remote.dto.AuthDireccionDto
import com.mango.fakestore.features.auth.data.remote.dto.AuthNombreDto
import com.mango.fakestore.features.auth.data.remote.dto.AuthUsuarioDto
import com.mango.fakestore.features.profile.data.local.PerfilDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SesionRepositoryImplTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val authApi: AuthApiService = mockk()
    private val perfilDao: PerfilDao = mockk(relaxed = true)
    private val dataStore: MangoDataStore = mockk(relaxed = true)
    private val sessionFlow = MutableStateFlow(SessionData.Empty)

    private lateinit var repositorio: SesionRepositoryImpl

    private val dtoEjemplo = AuthUsuarioDto(
        id = 3,
        username = "johnd",
        email = "john@example.com",
        phone = "1-570-236-7033",
        name = AuthNombreDto(firstname = "john", lastname = "doe"),
        address = AuthDireccionDto(city = "kilcoole", street = "new road", number = 7835, zipcode = "12926-3874"),
    )

    @Before
    fun setUp() {
        every { dataStore.sessionFlow } returns sessionFlow
        repositorio = SesionRepositoryImpl(authApi, perfilDao, dataStore)
    }

    @Test
    fun `dado API exitosa cuando se selecciona usuario entonces guarda perfil en BD y sesion en DataStore`() = runTest {
        coEvery { authApi.obtenerUsuario(3) } returns dtoEjemplo

        val resultado = repositorio.seleccionarUsuario(3)

        assertThat(resultado).isInstanceOf(Either.Right::class.java)
        coVerify { perfilDao.insertarPerfil(any()) }
        coVerify { dataStore.saveSession(any()) }
    }

    @Test
    fun `dado API exitosa cuando se selecciona usuario entonces SesionUsuario tiene activa=true`() = runTest {
        coEvery { authApi.obtenerUsuario(5) } returns dtoEjemplo.copy(id = 5)

        val resultado = repositorio.seleccionarUsuario(5)

        assertThat(resultado).isInstanceOf(Either.Right::class.java)
        val sesion = (resultado as Either.Right).value
        assertThat(sesion.userId).isEqualTo(5)
        assertThat(sesion.activa).isTrue()
    }

    @Test
    fun `dado sesion guardada cuando se observa sesion activa entonces emite el userId`() = runTest {
        sessionFlow.value = SessionData(accessToken = null, refreshToken = null, userId = "7")

        repositorio.obtenerSesionActiva().test {
            assertThat(awaitItem()).isEqualTo(7)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado sin sesion cuando se observa sesion activa entonces emite null`() = runTest {
        sessionFlow.value = SessionData.Empty

        repositorio.obtenerSesionActiva().test {
            assertThat(awaitItem()).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cuando se cierra sesion entonces llama clearSession en DataStore`() = runTest {
        val resultado = repositorio.cerrarSesion()

        assertThat(resultado).isInstanceOf(Either.Right::class.java)
        coVerify { dataStore.clearSession() }
    }
}
