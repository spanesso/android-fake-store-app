package com.mango.fakestore.features.products.presentation.viewmodel

import app.cash.turbine.test
import arrow.core.Either
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.core.testing.CoroutineTestRule
import com.mango.fakestore.features.products.domain.model.Producto
import com.mango.fakestore.features.products.domain.model.Valoracion
import com.mango.fakestore.features.products.domain.usecase.ObtenerProductos
import com.mango.fakestore.features.products.presentation.mapper.toUi
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiEffect
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiEvent
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductosViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val obtenerProductos: ObtenerProductos = mockk()
    private val telemetry: Telemetry = mockk(relaxed = true)
    private val errorMapper: DomainErrorToUiErrorMapper = mockk()

    // UiError de prueba reutilizable a lo largo del test
    private val uiErrorPrueba = UiError(
        messageRes = 0,
        severity = UiError.Severity.Blocking,
        actions = listOf(UiError.UiErrorAction.Retry),
        errorCode = "TEST-001",
    )

    // Productos de dominio de prueba
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
        // Configuración por defecto: el mapper siempre devuelve el UiError de prueba
        every { errorMapper.map(any()) } returns uiErrorPrueba
    }

    // -------------------------------------------------------------------------
    // 1. Estado inicial
    // -------------------------------------------------------------------------

    @Test
    fun `cuando se crea el viewmodel entonces emite Loading`() = kotlinx.coroutines.test.runTest {
        // Configuramos el usecase para que no emita nada de inmediato (flow vacío)
        every { obtenerProductos() } returns flow { /* no emite */ }

        val viewModel = ProductosViewModel(obtenerProductos, telemetry, errorMapper)

        viewModel.uiState.test {
            assertEquals(ProductosUiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // 2. Happy path — lista con productos
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve lista de productos cuando se carga entonces emite Content`() =
        kotlinx.coroutines.test.runTest {
            every { obtenerProductos() } returns flow {
                emit(Either.Right(productosDominio))
            }

            val viewModel = ProductosViewModel(obtenerProductos, telemetry, errorMapper)
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
    fun `dado repo devuelve lista vacia cuando se carga entonces emite Empty`() =
        kotlinx.coroutines.test.runTest {
            every { obtenerProductos() } returns flow {
                emit(Either.Right(emptyList()))
            }

            val viewModel = ProductosViewModel(obtenerProductos, telemetry, errorMapper)

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
    fun `dado repo devuelve NoConnection cuando se carga entonces emite Error`() =
        kotlinx.coroutines.test.runTest {
            val domainError = DomainError.Network.NoConnection()
            every { obtenerProductos() } returns flow {
                emit(Either.Left(domainError))
            }

            val viewModel = ProductosViewModel(obtenerProductos, telemetry, errorMapper)

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
    fun `dado repo devuelve Timeout cuando se carga entonces emite Error`() =
        kotlinx.coroutines.test.runTest {
            val domainError = DomainError.Network.Timeout()
            every { obtenerProductos() } returns flow {
                emit(Either.Left(domainError))
            }

            val viewModel = ProductosViewModel(obtenerProductos, telemetry, errorMapper)

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
    fun `dado repo devuelve Server error cuando se carga entonces emite Error`() =
        kotlinx.coroutines.test.runTest {
            val domainError = DomainError.Network.Server(httpCode = 500)
            every { obtenerProductos() } returns flow {
                emit(Either.Left(domainError))
            }

            val viewModel = ProductosViewModel(obtenerProductos, telemetry, errorMapper)

            viewModel.uiState.test {
                assertEquals(ProductosUiState.Loading, awaitItem())
                coroutineRule.dispatcher.scheduler.advanceUntilIdle()
                assertEquals(ProductosUiState.Error(uiErrorPrueba), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            verify(exactly = 1) { errorMapper.map(domainError) }
        }

    // -------------------------------------------------------------------------
    // 7. Retry — vuelve a emitir Loading y luego el resultado
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado de Error cuando onEvent Retry entonces vuelve a cargar emitiendo Loading`() =
        kotlinx.coroutines.test.runTest {
            val domainError = DomainError.Network.NoConnection()

            // Primera carga → error; segunda carga → lista de productos
            var invocaciones = 0
            every { obtenerProductos() } answers {
                invocaciones++
                if (invocaciones == 1) {
                    flow { emit(Either.Left(domainError)) }
                } else {
                    flow { emit(Either.Right(productosDominio)) }
                }
            }

            val viewModel = ProductosViewModel(obtenerProductos, telemetry, errorMapper)
            val productosEsperados = productosDominio.map { it.toUi() }

            viewModel.uiState.test {
                // Estado inicial del StateFlow
                assertEquals(ProductosUiState.Loading, awaitItem())

                // Ejecutar primera carga (error)
                coroutineRule.dispatcher.scheduler.advanceUntilIdle()
                assertEquals(ProductosUiState.Error(uiErrorPrueba), awaitItem())

                // Disparar Retry
                viewModel.onEvent(ProductosUiEvent.Retry)

                // Tras Retry debe volver a Loading
                assertEquals(ProductosUiState.Loading, awaitItem())

                // Ejecutar segunda carga (éxito)
                coroutineRule.dispatcher.scheduler.advanceUntilIdle()
                assertEquals(ProductosUiState.Content(productosEsperados), awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    // -------------------------------------------------------------------------
    // 8. Efecto lateral — MostrarSnackbar al recibir un error del repositorio
    // -------------------------------------------------------------------------

    @Test
    fun `dado repo devuelve error cuando se carga entonces emite MostrarSnackbar effect`() =
        kotlinx.coroutines.test.runTest {
            val domainError = DomainError.Network.NoConnection()
            every { obtenerProductos() } returns flow {
                emit(Either.Left(domainError))
            }

            val viewModel = ProductosViewModel(obtenerProductos, telemetry, errorMapper)

            // Usamos Turbine en el SharedFlow para garantizar que el collector esté
            // suscrito antes de que la corrutina interna emita el efecto.
            viewModel.uiEffect.test {
                coroutineRule.dispatcher.scheduler.advanceUntilIdle()
                val efecto = awaitItem()
                assertEquals(ProductosUiEffect.MostrarSnackbar(uiErrorPrueba), efecto)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
