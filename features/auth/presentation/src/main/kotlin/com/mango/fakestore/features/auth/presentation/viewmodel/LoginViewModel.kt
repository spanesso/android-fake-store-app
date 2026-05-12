package com.mango.fakestore.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mango.fakestore.core.analytics.AnalyticsEvent
import com.mango.fakestore.core.analytics.EventTracker
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.features.auth.domain.usecase.SeleccionarUsuario
import com.mango.fakestore.features.auth.presentation.model.UsuarioSeleccionUi
import com.mango.fakestore.features.auth.presentation.state.LoginUiEffect
import com.mango.fakestore.features.auth.presentation.state.LoginUiEvent
import com.mango.fakestore.features.auth.presentation.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
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
class LoginViewModel @Inject constructor(
    private val seleccionarUsuario: SeleccionarUsuario,
    private val telemetry: Telemetry,
    private val eventTracker: EventTracker,
    private val errorMapper: DomainErrorToUiErrorMapper,
) : ViewModel() {

    private val listaUsuarios = (1..10).map { id ->
        UsuarioSeleccionUi(id = id, etiqueta = "Usuario $id")
    }

    private val _uiState: MutableStateFlow<LoginUiState> =
        MutableStateFlow(LoginUiState.Idle(listaUsuarios))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<LoginUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<LoginUiEffect> = _uiEffect.asSharedFlow()

    private val errorHandler = CoroutineExceptionHandler { _, t ->
        val domainError = DomainError.Unknown(t)
        telemetry.reportarNoFatal(domainError, contexto = mapOf("vm" to "LoginViewModel"))
        _uiState.update { LoginUiState.Error(errorMapper.map(domainError)) }
    }

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.SeleccionarUsuario -> iniciarLogin(event.id)
            is LoginUiEvent.Retry -> _uiState.update { LoginUiState.Idle(listaUsuarios) }
        }
    }

    private fun iniciarLogin(userId: Int) {
        viewModelScope.launch(errorHandler) {
            _uiState.update { LoginUiState.Loading(userId) }
            seleccionarUsuario(userId).fold(
                ifLeft = { error ->
                    telemetry.reportarNoFatal(error, contexto = mapOf("vm" to "LoginViewModel", "userId" to "$userId"))
                    eventTracker.registrar(AnalyticsEvent.LoginFallido(error::class.simpleName ?: "unknown"))
                    _uiState.update { LoginUiState.Error(errorMapper.map(error)) }
                },
                ifRight = {
                    eventTracker.registrar(AnalyticsEvent.LoginExitoso)
                    _uiEffect.emit(LoginUiEffect.NavProductos)
                },
            )
        }
    }
}
