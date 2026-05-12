package com.mango.fakestore.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.favorites.domain.usecase.ObservarConteoFavoritos
import com.mango.fakestore.features.profile.domain.usecase.ObtenerPerfil
import com.mango.fakestore.features.profile.presentation.mapper.PerfilUiErrorMapper
import com.mango.fakestore.features.profile.presentation.model.PerfilContenidoUi
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiEffect
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiEvent
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiState
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val obtenerPerfil: ObtenerPerfil,
    private val observarConteoFavoritos: ObservarConteoFavoritos,
    private val telemetry: Telemetry,
    private val errorMapper: PerfilUiErrorMapper,
) : ViewModel() {

    private val _uiState: MutableStateFlow<PerfilUiState> =
        MutableStateFlow(PerfilUiState.Loading)
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<PerfilUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<PerfilUiEffect> = _uiEffect.asSharedFlow()

    private var cargaJob: Job? = null

    private val errorHandler = CoroutineExceptionHandler { _, t ->
        val domainError = DomainError.Unknown(t)
        telemetry.reportarNoFatal(
            error = domainError,
            contexto = mapOf("vm" to "PerfilViewModel"),
        )
        _uiState.update { PerfilUiState.Error(errorMapper.map(domainError)) }
    }

    init {
        cargarPerfil()
    }

    fun onEvent(event: PerfilUiEvent) {
        when (event) {
            is PerfilUiEvent.Retry -> cargarPerfil()
        }
    }

    private fun cargarPerfil() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch(errorHandler) {
            _uiState.update { PerfilUiState.Loading }
            combine(
                flow { emit(obtenerPerfil(PERFIL_USER_ID)) },
                observarConteoFavoritos(),
            ) { perfilResult, conteo ->
                perfilResult.fold(
                    ifLeft = { error ->
                        telemetry.reportarNoFatal(
                            error = error,
                            contexto = mapOf(
                                "vm" to "PerfilViewModel",
                                "accion" to "cargarPerfil",
                            ),
                        )
                        PerfilUiState.Error(errorMapper.map(error))
                    },
                    ifRight = { usuario ->
                        PerfilUiState.Content(
                            usuario = PerfilContenidoUi(
                                id = usuario.id,
                                nombreCompleto = usuario.nombreCompleto,
                                nombreUsuario = usuario.nombreUsuario,
                                email = usuario.email,
                                telefono = usuario.telefono,
                                ciudad = usuario.ciudad,
                                calle = usuario.calle,
                                codigoPostal = usuario.codigoPostal,
                                contadorFavoritos = conteo,
                            ),
                        )
                    },
                )
            }.collect { estado ->
                _uiState.update { estado }
            }
        }
    }

    companion object {
        private const val PERFIL_USER_ID = 8
    }
}
