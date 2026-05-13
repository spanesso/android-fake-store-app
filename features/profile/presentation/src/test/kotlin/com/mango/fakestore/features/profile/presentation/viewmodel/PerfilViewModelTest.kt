@file:Suppress("MaximumLineLength", "MaxLineLength")

package com.mango.fakestore.features.profile.presentation.viewmodel

import app.cash.turbine.test
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.analytics.AnalyticsEvent
import com.mango.fakestore.core.analytics.EventTracker
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.core.testing.CoroutineTestRule
import com.mango.fakestore.features.auth.domain.usecase.CerrarSesion
import com.mango.fakestore.features.auth.domain.usecase.ObtenerSesionActiva
import com.mango.fakestore.features.favorites.api.ObservarConteoFavoritos
import com.mango.fakestore.features.profile.domain.model.Usuario
import com.mango.fakestore.features.profile.domain.usecase.ObtenerPerfil
import com.mango.fakestore.features.profile.presentation.mapper.PerfilUiErrorMapper
import com.mango.fakestore.features.profile.presentation.model.PerfilContenidoUi
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiEffect
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiEvent
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PerfilViewModelTest {

    companion object {
        private const val NOMBRE_COMPLETO = "John Doe"
        private const val EMAIL = "john@example.com"
        private const val TELEFONO = "1-570-236-7033"
        private const val CALLE = "new road 7835"
        private const val CODIGO_POSTAL = "12926-3874"
    }

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val obtenerPerfil: ObtenerPerfil = mockk()
    private val obtenerSesionActiva: ObtenerSesionActiva = mockk()
    private val cerrarSesion: CerrarSesion = mockk()
    private val observarConteoFavoritos: ObservarConteoFavoritos = mockk()
    private val telemetry: Telemetry = mockk(relaxed = true)
    private val eventTracker: EventTracker = mockk(relaxed = true)
    private val errorMapper: PerfilUiErrorMapper = mockk()

    private val usuarioEjemplo = Usuario(
        id = 8,
        nombreCompleto = NOMBRE_COMPLETO,
        nombreUsuario = "johnd",
        email = EMAIL,
        telefono = TELEFONO,
        ciudad = "kilcoole",
        calle = CALLE,
        codigoPostal = CODIGO_POSTAL,
    )

    private val uiErrorNotFound = UiError(
        messageRes = 0,
        severity = UiError.Severity.Info,
        actions = listOf(UiError.UiErrorAction.Retry, UiError.UiErrorAction.Dismiss),
        errorCode = "NET-404",
    )

    private val uiErrorNoConnection = UiError(
        messageRes = 0,
        severity = UiError.Severity.Blocking,
        actions = listOf(UiError.UiErrorAction.Retry),
        errorCode = "NET-000",
    )

    @Before
    fun setUp() {
        every { obtenerSesionActiva() } returns flowOf(8)
        every { observarConteoFavoritos() } returns flowOf(0)
        every { errorMapper.map(any()) } returns uiErrorNoConnection
    }

    // -------------------------------------------------------------------------
    // 1. Estado inicial
    // -------------------------------------------------------------------------

    @Test
    fun `cuando se crea el viewmodel entonces emite Loading`() = runTest {
        coEvery { obtenerPerfil(any()) } returns Either.Right(usuarioEjemplo)

        val viewModel = crearViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(PerfilUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // 2. Happy path — Content con contador de favoritos reactivo
    // -------------------------------------------------------------------------

    @Test
    fun `dado repositorio devuelve usuario cuando se carga entonces emite Content con contadorFavoritos`() = runTest {
        every { observarConteoFavoritos() } returns flowOf(5)
        coEvery { obtenerPerfil(8) } returns Either.Right(usuarioEjemplo)

        val viewModel = crearViewModel()

        val contenidoEsperado = PerfilContenidoUi(
            id = 8,
            nombreCompleto = NOMBRE_COMPLETO,
            nombreUsuario = "johnd",
            email = EMAIL,
            telefono = TELEFONO,
            ciudad = "kilcoole",
            calle = CALLE,
            codigoPostal = CODIGO_POSTAL,
            contadorFavoritos = 5,
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(PerfilUiState.Loading)
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(PerfilUiState.Content(contenidoEsperado))
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // 3. Error Network.NotFound → Error con errorCode NET-404
    // -------------------------------------------------------------------------

    @Test
    fun `dado repositorio devuelve NotFound cuando se carga entonces emite Error con errorCode NET-404`() = runTest {
        val domainError = DomainError.Network.NotFound()
        coEvery { obtenerPerfil(8) } returns Either.Left(domainError)
        every { errorMapper.map(any()) } returns uiErrorNotFound

        val viewModel = crearViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(PerfilUiState.Loading)
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            val estado = awaitItem()
            assertThat(estado).isInstanceOf(PerfilUiState.Error::class.java)
            assertThat((estado as PerfilUiState.Error).error.errorCode).isEqualTo("NET-404")
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // 4. Error Network.NoConnection → Error con errorCode NET-000
    // -------------------------------------------------------------------------

    @Test
    fun `dado repositorio devuelve NoConnection cuando se carga entonces emite Error con errorCode NET-000`() = runTest {
        val domainError = DomainError.Network.NoConnection()
        coEvery { obtenerPerfil(8) } returns Either.Left(domainError)
        every { errorMapper.map(any()) } returns uiErrorNoConnection

        val viewModel = crearViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(PerfilUiState.Loading)
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            val estado = awaitItem()
            assertThat(estado).isInstanceOf(PerfilUiState.Error::class.java)
            assertThat((estado as PerfilUiState.Error).error.errorCode).isEqualTo("NET-000")
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // 5. Retry — Loading → Error → onEvent(Retry) → Loading → Content
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado Error cuando onEvent Retry entonces vuelve a cargar y emite Content`() = runTest {
        val domainError = DomainError.Network.NoConnection()
        var invocaciones = 0
        coEvery { obtenerPerfil(8) } answers {
            invocaciones++
            if (invocaciones == 1) Either.Left(domainError) else Either.Right(usuarioEjemplo)
        }
        every { errorMapper.map(any()) } returns uiErrorNoConnection

        val viewModel = crearViewModel()

        val contenidoEsperado = PerfilContenidoUi(
            id = 8,
            nombreCompleto = NOMBRE_COMPLETO,
            nombreUsuario = "johnd",
            email = EMAIL,
            telefono = TELEFONO,
            ciudad = "kilcoole",
            calle = CALLE,
            codigoPostal = CODIGO_POSTAL,
            contadorFavoritos = 0,
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(PerfilUiState.Loading)

            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(PerfilUiState.Error(uiErrorNoConnection))

            viewModel.onEvent(PerfilUiEvent.Retry)
            assertThat(awaitItem()).isEqualTo(PerfilUiState.Loading)

            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(PerfilUiState.Content(contenidoEsperado))

            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // US2 — Evento PerfilVisto
    // -------------------------------------------------------------------------

    @Test
    fun `cuando perfil carga con exito entonces registra PerfilVisto`() = runTest {
        coEvery { obtenerPerfil(8) } returns Either.Right(usuarioEjemplo)
        crearViewModel()
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()
        verify { eventTracker.registrar(AnalyticsEvent.PerfilVisto) }
    }

    @Test
    fun `cuando perfil falla entonces NO registra PerfilVisto`() = runTest {
        coEvery { obtenerPerfil(8) } returns Either.Left(DomainError.Network.NoConnection())
        crearViewModel()
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()
        verify(exactly = 0) { eventTracker.registrar(AnalyticsEvent.PerfilVisto) }
    }

    // -------------------------------------------------------------------------
    // 6. CerrarSesion — emite NavLogin tras ejecutar el caso de uso
    // -------------------------------------------------------------------------

    @Test
    fun `cuando onEvent CerrarSesion entonces llama cerrarSesion y emite NavLogin`() = runTest {
        coEvery { obtenerPerfil(any()) } returns Either.Right(usuarioEjemplo)
        coEvery { cerrarSesion() } returns Either.Right(Unit)

        val viewModel = crearViewModel()
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onEvent(PerfilUiEvent.CerrarSesion)
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(PerfilUiEffect.NavLogin)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun crearViewModel() = PerfilViewModel(
        obtenerPerfil = obtenerPerfil,
        obtenerSesionActiva = obtenerSesionActiva,
        cerrarSesion = cerrarSesion,
        observarConteoFavoritos = observarConteoFavoritos,
        telemetry = telemetry,
        eventTracker = eventTracker,
        errorMapper = errorMapper,
    )
}
