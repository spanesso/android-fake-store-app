package com.mango.fakestore.features.favorites.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.mango.fakestore.core.analytics.AnalyticsEvent
import com.mango.fakestore.core.analytics.EventTracker
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.features.favorites.domain.model.Favorito
import com.mango.fakestore.features.favorites.domain.usecase.ObservarFavoritos
import com.mango.fakestore.features.favorites.domain.usecase.ToggleFavorito
import com.mango.fakestore.features.favorites.presentation.mapper.toUi
import com.mango.fakestore.features.favorites.presentation.model.FavoritoUi
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiEffect
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiEvent
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiState
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
class FavoritosViewModel @Inject constructor(
    private val observarFavoritos: ObservarFavoritos,
    private val toggleFavorito: ToggleFavorito,
    private val telemetry: Telemetry,
    private val eventTracker: EventTracker,
    private val errorMapper: DomainErrorToUiErrorMapper,
) : ViewModel() {

    private val _uiState: MutableStateFlow<FavoritosUiState> =
        MutableStateFlow(FavoritosUiState.Loading)
    val uiState: StateFlow<FavoritosUiState> = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<FavoritosUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<FavoritosUiEffect> = _uiEffect.asSharedFlow()

    private var cargaJob: Job? = null

    private val errorHandler = CoroutineExceptionHandler { _, t ->
        val domainError = DomainError.Unknown(t)
        telemetry.reportarNoFatal(
            error = domainError,
            contexto = mapOf("vm" to "FavoritosViewModel"),
        )
        _uiState.update { FavoritosUiState.Error(errorMapper.map(domainError)) }
    }

    init {
        cargarFavoritos()
    }

    fun onEvent(event: FavoritosUiEvent) {
        when (event) {
            is FavoritosUiEvent.Cargar -> cargarFavoritos()
            is FavoritosUiEvent.Reintentar -> cargarFavoritos()
            is FavoritosUiEvent.ToggleFavorito -> toggleFavorito(event.favorito)
        }
    }

    private fun cargarFavoritos() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch(errorHandler) {
            _uiState.update { FavoritosUiState.Loading }
            observarFavoritos().collect { resultado ->
                when (resultado) {
                    is Either.Right -> {
                        val favoritos = resultado.value
                        _uiState.update {
                            if (favoritos.isEmpty()) {
                                FavoritosUiState.Empty
                            } else {
                                FavoritosUiState.Content(favoritos.map { it.toUi() })
                            }
                        }
                    }
                    is Either.Left -> {
                        val domainError = resultado.value
                        telemetry.reportarNoFatal(
                            error = domainError,
                            contexto = mapOf("vm" to "FavoritosViewModel", "accion" to "cargarFavoritos"),
                        )
                        _uiState.update { FavoritosUiState.Error(errorMapper.map(domainError)) }
                    }
                }
            }
        }
    }

    private fun toggleFavorito(favoritoUi: FavoritoUi) {
        viewModelScope.launch(errorHandler) {
            val traza = telemetry.iniciarTraza("toggle_favorito")
            try {
                val favorito = Favorito(
                    productoId = favoritoUi.productoId,
                    titulo = favoritoUi.titulo,
                    precio = favoritoUi.precio,
                    imagenUrl = favoritoUi.imagenUrl,
                    categoria = favoritoUi.categoria,
                    fechaMarcado = System.currentTimeMillis(),
                )
                val resultado = toggleFavorito(favorito)
                if (resultado is Either.Left) {
                    val uiError = errorMapper.map(resultado.value)
                    _uiEffect.emit(FavoritosUiEffect.MostrarSnackbar(uiError))
                } else {
                    // Favorito quita al hacer toggle desde la pantalla de favoritos
                    eventTracker.registrar(AnalyticsEvent.ProductoDesfavoritado(favoritoUi.productoId))
                }
            } finally {
                traza.detener()
            }
        }
    }
}
