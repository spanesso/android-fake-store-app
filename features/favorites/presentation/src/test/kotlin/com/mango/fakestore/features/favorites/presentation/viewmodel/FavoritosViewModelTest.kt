package com.mango.fakestore.features.favorites.presentation.viewmodel

import app.cash.turbine.test
import arrow.core.Either
import com.mango.fakestore.core.analytics.AnalyticsEvent
import com.mango.fakestore.core.analytics.EventTracker
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.analytics.TraceHandle
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.core.testing.CoroutineTestRule
import com.mango.fakestore.features.favorites.domain.model.Favorito
import com.mango.fakestore.features.favorites.domain.usecase.ObservarFavoritos
import com.mango.fakestore.features.favorites.domain.usecase.ToggleFavorito
import com.mango.fakestore.features.favorites.presentation.mapper.toUi
import com.mango.fakestore.features.favorites.presentation.model.FavoritoUi
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiEffect
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiEvent
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritosViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val observarFavoritos: ObservarFavoritos = mockk()
    private val toggleFavorito: ToggleFavorito = mockk()
    private val telemetry: Telemetry = mockk(relaxed = true)
    private val eventTracker: EventTracker = mockk(relaxed = true)
    private val traceHandle: TraceHandle = mockk(relaxed = true)
    private val errorMapper: DomainErrorToUiErrorMapper = mockk()

    private val uiErrorPrueba = UiError(
        messageRes = 0,
        severity = UiError.Severity.Blocking,
        actions = listOf(UiError.UiErrorAction.Retry),
        errorCode = "TEST-001",
    )

    private val favoritosDominio = listOf(
        Favorito(
            productoId = 1,
            titulo = "Camiseta Mango",
            precio = 29.99,
            imagenUrl = "https://example.com/camiseta.jpg",
            categoria = "ropa",
            fechaMarcado = 1_000_000L,
        ),
        Favorito(
            productoId = 2,
            titulo = "Pantalón Slim",
            precio = 59.99,
            imagenUrl = "https://example.com/pantalon.jpg",
            categoria = "ropa",
            fechaMarcado = 2_000_000L,
        ),
    )

    private val favoritoUiPrueba = FavoritoUi(
        productoId = 1,
        titulo = "Camiseta Mango",
        precio = 29.99,
        imagenUrl = "https://example.com/camiseta.jpg",
        categoria = "ropa",
    )

    @Before
    fun setUp() {
        every { errorMapper.map(any()) } returns uiErrorPrueba
        every { telemetry.iniciarTraza(any()) } returns traceHandle
    }

    // -------------------------------------------------------------------------
    // 1. Estado inicial
    // -------------------------------------------------------------------------

    @Test
    fun `cuando se crea el viewmodel entonces emite Loading`() =
        kotlinx.coroutines.test.runTest {
            every { observarFavoritos() } returns flow { /* no emite */ }

            val viewModel = crearViewModel()

            viewModel.uiState.test {
                assertEquals(FavoritosUiState.Loading, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    // -------------------------------------------------------------------------
    // 2. Happy path — lista con favoritos
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve favoritos cuando se carga entonces emite Content`() =
        kotlinx.coroutines.test.runTest {
            every { observarFavoritos() } returns flow {
                emit(Either.Right(favoritosDominio))
            }

            val viewModel = crearViewModel()
            val favoritosEsperados = favoritosDominio.map { it.toUi() }

            viewModel.uiState.test {
                assertEquals(FavoritosUiState.Loading, awaitItem())
                coroutineRule.dispatcher.scheduler.advanceUntilIdle()
                assertEquals(FavoritosUiState.Content(favoritosEsperados), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    // -------------------------------------------------------------------------
    // 3. Lista vacía → estado Empty
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve lista vacia cuando se carga entonces emite Empty`() =
        kotlinx.coroutines.test.runTest {
            every { observarFavoritos() } returns flow {
                emit(Either.Right(emptyList()))
            }

            val viewModel = crearViewModel()

            viewModel.uiState.test {
                assertEquals(FavoritosUiState.Loading, awaitItem())
                coroutineRule.dispatcher.scheduler.advanceUntilIdle()
                assertEquals(FavoritosUiState.Empty, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    // -------------------------------------------------------------------------
    // 4. Error de base de datos — ReadFailed → estado Error
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve ReadFailed cuando se carga entonces emite Error`() =
        kotlinx.coroutines.test.runTest {
            val domainError = DomainError.Database.ReadFailed()
            every { observarFavoritos() } returns flow {
                emit(Either.Left(domainError))
            }

            val viewModel = crearViewModel()

            viewModel.uiState.test {
                assertEquals(FavoritosUiState.Loading, awaitItem())
                coroutineRule.dispatcher.scheduler.advanceUntilIdle()
                assertEquals(FavoritosUiState.Error(uiErrorPrueba), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            verify(exactly = 1) { errorMapper.map(domainError) }
            verify(exactly = 1) {
                telemetry.reportarNoFatal(
                    error = domainError,
                    contexto = mapOf("vm" to "FavoritosViewModel", "accion" to "cargarFavoritos"),
                )
            }
        }

    // -------------------------------------------------------------------------
    // 5. Retry — vuelve a emitir Loading y luego el resultado
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado Error cuando onEvent Reintentar entonces vuelve a cargar`() =
        kotlinx.coroutines.test.runTest {
            val domainError = DomainError.Database.ReadFailed()
            var invocaciones = 0
            every { observarFavoritos() } answers {
                invocaciones++
                if (invocaciones == 1) {
                    flow { emit(Either.Left(domainError)) }
                } else {
                    flow { emit(Either.Right(favoritosDominio)) }
                }
            }

            val viewModel = crearViewModel()
            val favoritosEsperados = favoritosDominio.map { it.toUi() }

            viewModel.uiState.test {
                assertEquals(FavoritosUiState.Loading, awaitItem())

                coroutineRule.dispatcher.scheduler.advanceUntilIdle()
                assertEquals(FavoritosUiState.Error(uiErrorPrueba), awaitItem())

                viewModel.onEvent(FavoritosUiEvent.Reintentar)
                assertEquals(FavoritosUiState.Loading, awaitItem())

                coroutineRule.dispatcher.scheduler.advanceUntilIdle()
                assertEquals(FavoritosUiState.Content(favoritosEsperados), awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    // -------------------------------------------------------------------------
    // 6. Toggle falla — WriteFailed → MostrarSnackbar (sin cambiar uiState)
    // -------------------------------------------------------------------------

    @Test
    fun `dado toggle falla cuando onEvent ToggleFavorito entonces emite MostrarSnackbar`() =
        kotlinx.coroutines.test.runTest {
            every { observarFavoritos() } returns flow {
                emit(Either.Right(favoritosDominio))
            }
            val writeFailed = DomainError.Database.WriteFailed()
            coEvery { toggleFavorito(any()) } returns Either.Left(writeFailed)

            val viewModel = crearViewModel()

            viewModel.uiEffect.test {
                coroutineRule.dispatcher.scheduler.advanceUntilIdle()

                viewModel.onEvent(FavoritosUiEvent.ToggleFavorito(favoritoUiPrueba))
                coroutineRule.dispatcher.scheduler.advanceUntilIdle()

                val efecto = awaitItem()
                assertEquals(FavoritosUiEffect.MostrarSnackbar(uiErrorPrueba), efecto)
                cancelAndIgnoreRemainingEvents()
            }

            verify(exactly = 1) { errorMapper.map(writeFailed) }
        }

    // -------------------------------------------------------------------------
    // US3 — Trazas de rendimiento
    // -------------------------------------------------------------------------

    @Test
    fun `cuando toggle favorito entonces inicia y detiene traza toggle_favorito`() =
        kotlinx.coroutines.test.runTest {
            every { observarFavoritos() } returns flow {
                emit(Either.Right(favoritosDominio))
            }
            coEvery { toggleFavorito(any()) } returns Either.Right(Unit)

            val viewModel = crearViewModel()
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(FavoritosUiEvent.ToggleFavorito(favoritoUiPrueba))
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()

            verify { telemetry.iniciarTraza("toggle_favorito") }
            verify { traceHandle.detener() }
        }

    @Test
    fun `cuando toggle favorito tiene exito entonces registra ProductoDesfavoritado`() =
        kotlinx.coroutines.test.runTest {
            every { observarFavoritos() } returns flow {
                emit(Either.Right(favoritosDominio))
            }
            coEvery { toggleFavorito(any()) } returns Either.Right(Unit)

            val viewModel = crearViewModel()
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(FavoritosUiEvent.ToggleFavorito(favoritoUiPrueba))
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()

            verify { eventTracker.registrar(AnalyticsEvent.ProductoDesfavoritado(favoritoUiPrueba.productoId)) }
        }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun crearViewModel() = FavoritosViewModel(
        observarFavoritos = observarFavoritos,
        toggleFavorito = toggleFavorito,
        telemetry = telemetry,
        eventTracker = eventTracker,
        errorMapper = errorMapper,
    )
}
