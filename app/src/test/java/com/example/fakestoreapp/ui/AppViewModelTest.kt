package com.example.fakestoreapp.ui

import app.cash.turbine.test
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.core.network.connectivity.ConnectivityObserver
import com.mango.fakestore.core.network.connectivity.ConnectivityStatus
import com.mango.fakestore.core.security.biometric.BiometricAuthenticator
import com.mango.fakestore.core.security.biometric.BiometricResult
import com.mango.fakestore.core.testing.CoroutineTestRule
import com.mango.fakestore.features.favorites.domain.usecase.ObservarConteoFavoritos
import io.mockk.coEvery
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

    private val connectivityObserver: ConnectivityObserver = mockk {
        every { statusFlow } returns conectividadFlow
    }

    private val biometricAuthenticator: BiometricAuthenticator = mockk()

    private val telemetry: Telemetry = mockk(relaxed = true)

    private val uiError = mockk<UiError>(relaxed = true)

    private val errorMapper: DomainErrorToUiErrorMapper = mockk {
        every { map(any()) } returns uiError
    }

    private val observarConteoFavoritos: ObservarConteoFavoritos = mockk()

    @Before
    fun setUp() {
        every { observarConteoFavoritos() } returns flowOf(0)
    }

    private fun crearViewModel() = AppViewModel(
        connectivityObserver = connectivityObserver,
        biometricAuthenticator = biometricAuthenticator,
        telemetry = telemetry,
        errorMapper = errorMapper,
        observarConteoFavoritos = observarConteoFavoritos,
    )

    // ── US3: Conectividad ──────────────────────────────────────────────────

    @Test
    fun `dado Connected cuando observa entonces isOffline es false`() = runTest {
        conectividadFlow.value = ConnectivityStatus.Connected
        val viewModel = crearViewModel()

        viewModel.isOffline.test {
            // stateIn initial value = false, then upstream confirms false
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado Disconnected cuando observa entonces isOffline es true`() = runTest {
        conectividadFlow.value = ConnectivityStatus.Disconnected
        val viewModel = crearViewModel()

        viewModel.isOffline.test {
            // stateIn emits initialValue=false first, then upstream emits true
            awaitItem()  // skip initialValue
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado Unavailable cuando observa entonces isOffline es true`() = runTest {
        conectividadFlow.value = ConnectivityStatus.Unavailable
        val viewModel = crearViewModel()

        viewModel.isOffline.test {
            // stateIn emits initialValue=false first, then upstream emits true
            awaitItem()  // skip initialValue
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado recupera conexion entonces isOffline vuelve a false`() = runTest {
        conectividadFlow.value = ConnectivityStatus.Disconnected
        val viewModel = crearViewModel()

        viewModel.isOffline.test {
            awaitItem()  // skip initialValue=false
            assertTrue(awaitItem())  // upstream: Disconnected -> true
            conectividadFlow.value = ConnectivityStatus.Connected
            assertFalse(awaitItem())  // upstream: Connected -> false
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── US2: Gateway biométrico ────────────────────────────────────────────

    @Test
    fun `dado biometria exitosa cuando autentica entonces sesionAutenticada es true`() = runTest {
        val actividad = mockk<androidx.fragment.app.FragmentActivity>(relaxed = true) {
            every { getString(any()) } returns "test"
        }
        coEvery {
            biometricAuthenticator.autenticar(any(), any(), any(), any())
        } returns BiometricResult.Exito
        val viewModel = crearViewModel()

        val resultado = viewModel.autenticarParaPerfil(actividad)

        assertEquals(BiometricResult.Exito, resultado)
        assertTrue(viewModel.sesionAutenticada)
    }

    @Test
    fun `dado biometria cancelada cuando autentica entonces sesionAutenticada es false`() = runTest {
        val actividad = mockk<androidx.fragment.app.FragmentActivity>(relaxed = true) {
            every { getString(any()) } returns "test"
        }
        coEvery {
            biometricAuthenticator.autenticar(any(), any(), any(), any())
        } returns BiometricResult.Cancelado
        val viewModel = crearViewModel()

        val resultado = viewModel.autenticarParaPerfil(actividad)

        assertEquals(BiometricResult.Cancelado, resultado)
        assertFalse(viewModel.sesionAutenticada)
    }

    @Test
    fun `dado biometria bloqueada cuando autentica entonces sesionAutenticada es false`() = runTest {
        val actividad = mockk<androidx.fragment.app.FragmentActivity>(relaxed = true) {
            every { getString(any()) } returns "test"
        }
        coEvery {
            biometricAuthenticator.autenticar(any(), any(), any(), any())
        } returns BiometricResult.BloqueadoTemporalmente
        val viewModel = crearViewModel()

        val resultado = viewModel.autenticarParaPerfil(actividad)

        assertEquals(BiometricResult.BloqueadoTemporalmente, resultado)
        assertFalse(viewModel.sesionAutenticada)
    }

    // ── US4: Handler global de errores ─────────────────────────────────────

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
