package com.example.fakestoreapp.ui

import app.cash.turbine.test
import com.example.fakestoreapp.ui.navigation.AppRoute
import com.mango.fakestore.core.analytics.EventTracker
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.core.network.connectivity.ConnectivityObserver
import com.mango.fakestore.core.network.connectivity.ConnectivityStatus
import com.mango.fakestore.core.security.integrity.IntegrityChecker
import com.mango.fakestore.core.security.integrity.IntegrityResult
import com.mango.fakestore.core.testing.CoroutineTestRule
import com.mango.fakestore.features.auth.domain.usecase.ObtenerSesionActiva
import com.mango.fakestore.features.favorites.domain.usecase.ObservarConteoFavoritos
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val conectividadFlow = MutableStateFlow<ConnectivityStatus>(ConnectivityStatus.Connected)
    private val sesionFlow = MutableStateFlow<Int?>(null)

    private val connectivityObserver: ConnectivityObserver = mockk {
        every { statusFlow } returns conectividadFlow
    }

    private val obtenerSesionActiva: ObtenerSesionActiva = mockk()

    private val telemetry: Telemetry = mockk(relaxed = true)
    private val eventTracker: EventTracker = mockk(relaxed = true)
    private val uiError = mockk<UiError>(relaxed = true)
    private val errorMapper: DomainErrorToUiErrorMapper = mockk { every { map(any()) } returns uiError }
    private val observarConteoFavoritos: ObservarConteoFavoritos = mockk()
    private val integrityChecker: IntegrityChecker = mockk {
        every { verificarIntegridad() } returns IntegrityResult.INTEGRA
    }

    @Before
    fun setUp() {
        every { obtenerSesionActiva() } returns sesionFlow
        every { observarConteoFavoritos() } returns flowOf(0)
    }

    private fun crearViewModel() = AppViewModel(
        connectivityObserver = connectivityObserver,
        telemetry = telemetry,
        eventTracker = eventTracker,
        errorMapper = errorMapper,
        integrityChecker = integrityChecker,
        obtenerSesionActiva = obtenerSesionActiva,
        observarConteoFavoritos = observarConteoFavoritos,
    )

    // ── Conectividad ───────────────────────────────────────────────────────

    @Test
    fun `dado Connected cuando observa entonces isOffline es false`() = runTest {
        conectividadFlow.value = ConnectivityStatus.Connected
        val viewModel = crearViewModel()
        viewModel.isOffline.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado Disconnected cuando observa entonces isOffline es true`() = runTest {
        conectividadFlow.value = ConnectivityStatus.Disconnected
        val viewModel = crearViewModel()
        viewModel.isOffline.test {
            awaitItem()
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado Unavailable cuando observa entonces isOffline es true`() = runTest {
        conectividadFlow.value = ConnectivityStatus.Unavailable
        val viewModel = crearViewModel()
        viewModel.isOffline.test {
            awaitItem()
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado recupera conexion entonces isOffline vuelve a false`() = runTest {
        conectividadFlow.value = ConnectivityStatus.Disconnected
        val viewModel = crearViewModel()
        viewModel.isOffline.test {
            awaitItem()
            assertTrue(awaitItem())
            conectividadFlow.value = ConnectivityStatus.Connected
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── startDestination — sesión ──────────────────────────────────────────

    @Test
    fun `dado sin sesion activa entonces startDestination es Login`() = runTest {
        sesionFlow.value = null
        val viewModel = crearViewModel()
        viewModel.startDestination.test {
            assertEquals(AppRoute.Login, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado sesion activa entonces startDestination es Productos`() = runTest {
        sesionFlow.value = 1
        val viewModel = crearViewModel()
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.startDestination.test {
            assertEquals(AppRoute.Productos, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado sesion que cambia de null a userId entonces startDestination cambia de Login a Productos`() = runTest {
        sesionFlow.value = null
        val viewModel = crearViewModel()
        viewModel.startDestination.test {
            assertEquals(AppRoute.Login, awaitItem())
            sesionFlow.value = 5
            assertEquals(AppRoute.Productos, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Handler global de errores ──────────────────────────────────────────

    @Test
    fun `dado throwable inesperado cuando reportarErrorGlobal entonces emite MostrarErrorGlobal`() = runTest {
        val viewModel = crearViewModel()
        val throwable = RuntimeException("error de prueba")
        viewModel.uiEffect.test {
            viewModel.reportarErrorGlobal(throwable)
            val efecto = awaitItem()
            assertTrue(efecto is AppUiEffect.MostrarErrorGlobal)
            assertEquals(uiError, (efecto as AppUiEffect.MostrarErrorGlobal).uiError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado throwable cuando reportarErrorGlobal entonces telemetria reporta noFatal`() = runTest {
        val viewModel = crearViewModel()
        val throwable = RuntimeException("error de telemetría")
        viewModel.reportarErrorGlobal(throwable)
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()
        verify { telemetry.reportarNoFatal(any()) }
    }
}
