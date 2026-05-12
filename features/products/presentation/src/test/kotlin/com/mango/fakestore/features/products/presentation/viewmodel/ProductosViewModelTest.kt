@file:Suppress("MaximumLineLength", "MaxLineLength")

package com.mango.fakestore.features.products.presentation.viewmodel

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
import com.mango.fakestore.features.favorites.domain.usecase.ObservarFavoritos
import com.mango.fakestore.features.favorites.domain.usecase.ToggleFavorito
import com.mango.fakestore.features.products.domain.model.Producto
import com.mango.fakestore.features.products.domain.model.Valoracion
import com.mango.fakestore.features.products.domain.usecase.ObtenerProductos
import com.mango.fakestore.features.products.presentation.mapper.toUi
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiEffect
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiEvent
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductosViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val obtenerProductos: ObtenerProductos = mockk()
    private val observarFavoritos: ObservarFavoritos = mockk()
    private val toggleFavorito: ToggleFavorito = mockk()
    private val telemetry: Telemetry = mockk(relaxed = true)
    private val eventTracker: EventTracker = mockk(relaxed = true)
    private val errorMapper: DomainErrorToUiErrorMapper = mockk()
    private val traceHandle: TraceHandle = mockk(relaxed = true)

    private val uiErrorPrueba = UiError(
        messageRes = 0,
        severity = UiError.Severity.Blocking,
        actions = listOf(UiError.UiErrorAction.Retry),
        errorCode = "TEST-001",
    )

    private val productosDominio = listOf(
        Producto(
            id = 1,
            titulo = "Camiseta Mango",
            descripcion = "Camiseta de algodón de calidad premium",
            precio = 29.99,
            categoria = "ropa",
            imagenUrl = "https://example.com/camiseta.jpg",
            valoracion = Valoracion(puntuacion = 4.5, numVotaciones = 120),
        ),
        Producto(
            id = 2,
            titulo = "Pantalón Slim",
            descripcion = "Pantalón de corte slim moderno",
            precio = 59.99,
            categoria = "ropa",
            imagenUrl = "https://example.com/pantalon.jpg",
            valoracion = Valoracion(puntuacion = 4.2, numVotaciones = 85),
        ),
    )

    @Before
    fun setUp() {
        every { errorMapper.map(any()) } returns uiErrorPrueba
        every { observarFavoritos() } returns flow { emit(Either.Right(emptyList())) }
        every { telemetry.iniciarTraza(any()) } returns traceHandle
    }

    private fun crearViewModel() = ProductosViewModel(
        obtenerProductos,
        observarFavoritos,
        toggleFavorito,
        telemetry,
        eventTracker,
        errorMapper,
    )

    // -------------------------------------------------------------------------
    // 1. Estado inicial
    // -------------------------------------------------------------------------

    @Test
    fun `cuando se crea el viewmodel entonces emite Loading`() = runTest {
        every { obtenerProductos() } returns flow { /* no emite */ }
        val viewModel = crearViewModel()
        viewModel.uiState.test {
            assertEquals(ProductosUiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // 2. Happy path — lista con productos
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve lista de productos cuando se carga entonces emite Content`() = runTest {
        every { obtenerProductos() } returns flow { emit(Either.Right(productosDominio)) }
        val viewModel = crearViewModel()
        val productosEsperados = productosDominio.map { it.toUi() }
        viewModel.uiState.test {
            assertEquals(ProductosUiState.Loading, awaitItem())
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(ProductosUiState.Content(productosEsperados), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // 3. Lista vacía → estado Empty
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve lista vacia cuando se carga entonces emite Empty`() = runTest {
        every { obtenerProductos() } returns flow { emit(Either.Right(emptyList())) }
        val viewModel = crearViewModel()
        viewModel.uiState.test {
            assertEquals(ProductosUiState.Loading, awaitItem())
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(ProductosUiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // 4. Error — sin conexión
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve NoConnection cuando se carga entonces emite Error`() = runTest {
        val domainError = DomainError.Network.NoConnection()
        every { obtenerProductos() } returns flow { emit(Either.Left(domainError)) }
        val viewModel = crearViewModel()
        viewModel.uiState.test {
            assertEquals(ProductosUiState.Loading, awaitItem())
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(ProductosUiState.Error(uiErrorPrueba), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(exactly = 1) { errorMapper.map(domainError) }
        verify(exactly = 1) {
            telemetry.reportarNoFatal(
                error = domainError,
                contexto = mapOf("vm" to "ProductosViewModel", "accion" to "cargarProductos"),
            )
        }
    }

    // -------------------------------------------------------------------------
    // 5. Error — timeout
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve Timeout cuando se carga entonces emite Error`() = runTest {
        val domainError = DomainError.Network.Timeout()
        every { obtenerProductos() } returns flow { emit(Either.Left(domainError)) }
        val viewModel = crearViewModel()
        viewModel.uiState.test {
            assertEquals(ProductosUiState.Loading, awaitItem())
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(ProductosUiState.Error(uiErrorPrueba), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(exactly = 1) { errorMapper.map(domainError) }
    }

    // -------------------------------------------------------------------------
    // 6. Error — error de servidor
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve Server error cuando se carga entonces emite Error`() = runTest {
        val domainError = DomainError.Network.Server(httpCode = 500)
        every { obtenerProductos() } returns flow { emit(Either.Left(domainError)) }
        val viewModel = crearViewModel()
        viewModel.uiState.test {
            assertEquals(ProductosUiState.Loading, awaitItem())
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(ProductosUiState.Error(uiErrorPrueba), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(exactly = 1) { errorMapper.map(domainError) }
    }

    // -------------------------------------------------------------------------
    // 7. Retry
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado de Error cuando onEvent Retry entonces vuelve a cargar emitiendo Loading`() = runTest {
        val domainError = DomainError.Network.NoConnection()
        var invocaciones = 0
        every { obtenerProductos() } answers {
            invocaciones++
            if (invocaciones == 1) {
                flow { emit(Either.Left(domainError)) }
            } else {
                flow { emit(Either.Right(productosDominio)) }
            }
        }
        val viewModel = crearViewModel()
        val productosEsperados = productosDominio.map { it.toUi() }
        viewModel.uiState.test {
            assertEquals(ProductosUiState.Loading, awaitItem())
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(ProductosUiState.Error(uiErrorPrueba), awaitItem())
            viewModel.onEvent(ProductosUiEvent.Retry)
            assertEquals(ProductosUiState.Loading, awaitItem())
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(ProductosUiState.Content(productosEsperados), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // 8. Efecto lateral — MostrarSnackbar
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve error cuando se carga entonces emite MostrarSnackbar effect`() = runTest {
        val domainError = DomainError.Network.NoConnection()
        every { obtenerProductos() } returns flow { emit(Either.Left(domainError)) }
        val viewModel = crearViewModel()
        viewModel.uiEffect.test {
            coroutineRule.dispatcher.scheduler.advanceUntilIdle()
            val efecto = awaitItem()
            assertEquals(ProductosUiEffect.MostrarSnackbar(uiErrorPrueba), efecto)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // US2 — Eventos de Analytics
    // -------------------------------------------------------------------------

    @Test
    fun `cuando lista carga con productos entonces registra ProductoVisto`() = runTest {
        every { obtenerProductos() } returns flow { emit(Either.Right(productosDominio)) }
        val viewModel = crearViewModel()
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()
        verify { eventTracker.registrar(AnalyticsEvent.ProductoVisto(productosDominio.first().id)) }
    }

    @Test
    fun `cuando toggle favorito tiene exito en producto no favorito entonces registra ProductoFavoritado`() = runTest {
        every { obtenerProductos() } returns flow { emit(Either.Right(productosDominio)) }
        coEvery { toggleFavorito(any()) } returns Either.Right(Unit)
        val viewModel = crearViewModel()
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        val productoUi = productosDominio.first().toUi(esFavorito = false)
        viewModel.onEvent(ProductosUiEvent.ToggleFavorito(productoUi))
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        verify { eventTracker.registrar(AnalyticsEvent.ProductoFavoritado(productosDominio.first().id)) }
    }

    @Test
    fun `cuando toggle favorito tiene exito en producto ya favorito entonces registra ProductoDesfavoritado`() = runTest {
        every { obtenerProductos() } returns flow { emit(Either.Right(productosDominio)) }
        coEvery { toggleFavorito(any()) } returns Either.Right(Unit)
        val viewModel = crearViewModel()
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        val productoUi = productosDominio.first().toUi(esFavorito = true)
        viewModel.onEvent(ProductosUiEvent.ToggleFavorito(productoUi))
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        verify { eventTracker.registrar(AnalyticsEvent.ProductoDesfavoritado(productosDominio.first().id)) }
    }

    // -------------------------------------------------------------------------
    // US3 — Trazas de rendimiento
    // -------------------------------------------------------------------------

    @Test
    fun `cuando se cargan productos entonces inicia y detiene traza cargar_productos`() = runTest {
        every { obtenerProductos() } returns flow { emit(Either.Right(productosDominio)) }
        val viewModel = crearViewModel()
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()
        verify { telemetry.iniciarTraza("cargar_productos") }
        verify { traceHandle.detener() }
    }

    @Test
    fun `cuando toggle favorito entonces inicia y detiene traza toggle_favorito`() = runTest {
        every { obtenerProductos() } returns flow { emit(Either.Right(productosDominio)) }
        coEvery { toggleFavorito(any()) } returns Either.Right(Unit)
        val viewModel = crearViewModel()
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        val productoUi = productosDominio.first().toUi()
        viewModel.onEvent(ProductosUiEvent.ToggleFavorito(productoUi))
        coroutineRule.dispatcher.scheduler.advanceUntilIdle()

        verify { telemetry.iniciarTraza("toggle_favorito") }
    }
}
