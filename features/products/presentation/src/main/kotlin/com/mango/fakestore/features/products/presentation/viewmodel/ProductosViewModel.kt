package com.mango.fakestore.features.products.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.features.products.domain.usecase.ObtenerProductos
import com.mango.fakestore.features.products.presentation.mapper.toUi
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductosViewModel @Inject constructor(
    private val obtenerProductos: ObtenerProductos,
    private val telemetry: Telemetry,
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
        }
    }

    private fun cargarProductos() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch(errorHandler) {
            _uiState.update { ProductosUiState.Loading }
            obtenerProductos().collect { resultado ->
                when (resultado) {
                    is Either.Right -> {
                        val productos = resultado.value
                        _uiState.update {
                            if (productos.isEmpty()) {
                                ProductosUiState.Empty
                            } else {
                                ProductosUiState.Content(productos.map { it.toUi() })
                            }
                        }
                    }
                    is Either.Left -> {
                        val domainError = resultado.value
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
        }
    }
}
