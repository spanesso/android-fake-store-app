package com.mango.fakestore.features.products.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.mango.fakestore.core.analytics.AnalyticsEvent
import com.mango.fakestore.core.analytics.EventTracker
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.features.favorites.api.Favorito
import com.mango.fakestore.features.favorites.api.ObservarFavoritos
import com.mango.fakestore.features.favorites.api.ToggleFavorito
import com.mango.fakestore.features.products.domain.usecase.ObtenerProductos
import com.mango.fakestore.features.products.presentation.mapper.toUi
import com.mango.fakestore.features.products.presentation.model.ProductoUi
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiEffect
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiEvent
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductosViewModel @Inject constructor(
    private val obtenerProductos: ObtenerProductos,
    private val observarFavoritos: ObservarFavoritos,
    private val toggleFavorito: ToggleFavorito,
    private val telemetry: Telemetry,
    private val eventTracker: EventTracker,
    private val errorMapper: DomainErrorToUiErrorMapper,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProductosUiState> =
        MutableStateFlow(ProductosUiState.Loading)
    val uiState: StateFlow<ProductosUiState> = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<ProductosUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<ProductosUiEffect> = _uiEffect.asSharedFlow()

    private var cargaJob: Job? = null

    private val errorHandler = CoroutineExceptionHandler { _, t ->
        val domainError = DomainError.Unknown(t)
        telemetry.reportarNoFatal(
            error = domainError,
            contexto = mapOf("vm" to "ProductosViewModel"),
        )
        _uiState.update { ProductosUiState.Error(errorMapper.map(domainError)) }
    }

    init {
        cargarProductos()
    }

    fun onEvent(event: ProductosUiEvent) {
        when (event) {
            is ProductosUiEvent.Retry -> cargarProductos()
            is ProductosUiEvent.Refrescar -> cargarProductos()
            is ProductosUiEvent.ToggleFavorito -> toggleFavorito(event.producto)
        }
    }

    private fun cargarProductos() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch(errorHandler) {
            _uiState.update { ProductosUiState.Loading }
            val traza = telemetry.iniciarTraza("cargar_productos")
            try {
                combine(obtenerProductos(), observarFavoritos()) { productosResult, favoritosResult ->
                    productosResult to favoritosResult
                }.collect { (productosResult, favoritosResult) ->
                    when (productosResult) {
                        is Either.Right -> {
                            val productos = productosResult.value
                            val favoritosIds = when (favoritosResult) {
                                is Either.Right -> favoritosResult.value.map { it.productoId }.toSet()
                                is Either.Left -> emptySet()
                            }
                            _uiState.update {
                                if (productos.isEmpty()) {
                                    ProductosUiState.Empty
                                } else {
                                    // Sin pantalla de detalle: registrar el primer producto visible
                                    eventTracker.registrar(AnalyticsEvent.ProductoVisto(productos.first().id))
                                    ProductosUiState.Content(
                                        productos.map { it.toUi(esFavorito = it.id in favoritosIds) },
                                    )
                                }
                            }
                        }
                        is Either.Left -> {
                            val domainError = productosResult.value
                            telemetry.reportarNoFatal(
                                error = domainError,
                                contexto = mapOf("vm" to "ProductosViewModel", "accion" to "cargarProductos"),
                            )
                            val uiError = errorMapper.map(domainError)
                            _uiState.update { ProductosUiState.Error(uiError) }
                            _uiEffect.emit(ProductosUiEffect.MostrarSnackbar(uiError))
                        }
                    }
                }
            } finally {
                traza.detener()
            }
        }
    }

    private fun toggleFavorito(productoUi: ProductoUi) {
        viewModelScope.launch(errorHandler) {
            val traza = telemetry.iniciarTraza("toggle_favorito")
            try {
                val favorito = Favorito(
                    productoId = productoUi.id,
                    titulo = productoUi.titulo,
                    precio = productoUi.precioDouble,
                    imagenUrl = productoUi.imagenUrl,
                    categoria = productoUi.categoria,
                    fechaMarcado = System.currentTimeMillis(),
                )
                val resultado = toggleFavorito(favorito)
                if (resultado is Either.Left) {
                    val uiError = errorMapper.map(resultado.value)
                    _uiEffect.emit(ProductosUiEffect.MostrarSnackbar(uiError))
                } else {
                    val evento = if (productoUi.esFavorito) {
                        AnalyticsEvent.ProductoDesfavoritado(productoUi.id)
                    } else {
                        AnalyticsEvent.ProductoFavoritado(productoUi.id)
                    }
                    eventTracker.registrar(evento)
                }
            } finally {
                traza.detener()
            }
        }
    }
}
