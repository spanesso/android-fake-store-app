package com.mango.fakestore.features.auth.presentation.viewmodel

import app.cash.turbine.test
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.analytics.EventTracker
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.core.testing.CoroutineTestRule
import com.mango.fakestore.features.auth.domain.model.SesionUsuario
import com.mango.fakestore.features.auth.domain.usecase.SeleccionarUsuario
import com.mango.fakestore.features.auth.presentation.state.LoginUiEffect
import com.mango.fakestore.features.auth.presentation.state.LoginUiEvent
import com.mango.fakestore.features.auth.presentation.state.LoginUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val seleccionarUsuario: SeleccionarUsuario = mockk()
    private val telemetry: Telemetry = mockk(relaxed = true)
    private val eventTracker: EventTracker = mockk(relaxed = true)
    private val uiError = mockk<UiError>(relaxed = true)
    private val errorMapper: DomainErrorToUiErrorMapper = mockk { every { map(any()) } returns uiError }

    private fun crearViewModel() = LoginViewModel(
        seleccionarUsuario = seleccionarUsuario,
        telemetry = telemetry,
        eventTracker = eventTracker,
        errorMapper = errorMapper,
    )

    @Test
    fun `cuando se crea el viewmodel entonces emite Idle con 10 usuarios`() = runTest {
        val viewModel = crearViewModel()

        viewModel.uiState.test {
            val estado = awaitItem()
            assertThat(estado).isInstanceOf(LoginUiState.Idle::class.java)
            assertThat((estado as LoginUiState.Idle).usuarios).hasSize(10)
            assertThat(estado.usuarios.first().id).isEqualTo(1)
            assertThat(estado.usuarios.last().id).isEqualTo(10)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado click en usuario cuando login exitoso entonces emite Loading luego effect NavProductos`() = runTest {
        val sesion = SesionUsuario(userId = 3, activa = true)
        coEvery { seleccionarUsuario(3) } returns Either.Right(sesion)

        val viewModel = crearViewModel()

        viewModel.uiEffect.test {
            viewModel.onEvent(LoginUiEvent.SeleccionarUsuario(3))
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(LoginUiEffect.NavProductos)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado click en usuario cuando login falla entonces emite Loading luego Error`() = runTest {
        val error = DomainError.Network.NoConnection()
        coEvery { seleccionarUsuario(1) } returns Either.Left(error)

        val viewModel = crearViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(LoginUiState.Idle::class.java)

            viewModel.onEvent(LoginUiEvent.SeleccionarUsuario(1))
            assertThat(awaitItem()).isInstanceOf(LoginUiState.Loading::class.java)

            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(LoginUiState.Error::class.java)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dado estado Error cuando onEvent Retry entonces vuelve a Idle con 10 usuarios`() = runTest {
        val error = DomainError.Network.NoConnection()
        coEvery { seleccionarUsuario(any()) } returns Either.Left(error)

        val viewModel = crearViewModel()
        viewModel.onEvent(LoginUiEvent.SeleccionarUsuario(2))
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.onEvent(LoginUiEvent.Retry)

        viewModel.uiState.test {
            val estado = awaitItem()
            assertThat(estado).isInstanceOf(LoginUiState.Idle::class.java)
            assertThat((estado as LoginUiState.Idle).usuarios).hasSize(10)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
