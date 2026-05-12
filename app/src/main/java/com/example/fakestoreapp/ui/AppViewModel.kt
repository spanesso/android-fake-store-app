package com.example.fakestoreapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.example.fakestoreapp.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.core.network.connectivity.ConnectivityObserver
import com.mango.fakestore.core.network.connectivity.ConnectivityStatus
import com.mango.fakestore.core.security.biometric.BiometricAuthenticator
import com.mango.fakestore.core.security.biometric.BiometricResult
import com.mango.fakestore.features.favorites.domain.usecase.ObservarConteoFavoritos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val connectivityObserver: ConnectivityObserver,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val telemetry: Telemetry,
    private val errorMapper: DomainErrorToUiErrorMapper,
    observarConteoFavoritos: ObservarConteoFavoritos,
) : ViewModel() {

    val isOffline: StateFlow<Boolean> = connectivityObserver.statusFlow
        .map { it != ConnectivityStatus.Connected }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val contadorFavoritos: StateFlow<Int> = observarConteoFavoritos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )

    private val _uiEffect = MutableSharedFlow<AppUiEffect>()
    val uiEffect: SharedFlow<AppUiEffect> = _uiEffect.asSharedFlow()

    var sesionAutenticada by mutableStateOf(false)
        private set

    suspend fun autenticarParaPerfil(actividad: FragmentActivity): BiometricResult {
        val resultado = biometricAuthenticator.autenticar(
            actividad = actividad,
            titulo = actividad.getString(R.string.biometria_titulo),
            subtitulo = actividad.getString(R.string.biometria_subtitulo),
            cancelarTexto = actividad.getString(R.string.biometria_cancelar),
        )
        if (resultado is BiometricResult.Exito) {
            sesionAutenticada = true
        }
        return resultado
    }

    fun reportarErrorGlobal(throwable: Throwable) {
        val domainError = DomainError.Unknown(throwable)
        telemetry.reportarNoFatal(domainError)
        val uiError = errorMapper.map(domainError)
        viewModelScope.launch {
            _uiEffect.emit(AppUiEffect.MostrarErrorGlobal(uiError))
        }
    }
}
