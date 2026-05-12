package com.example.fakestoreapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestoreapp.ui.navigation.AppRoute
import com.mango.fakestore.core.analytics.EventTracker
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.core.network.connectivity.ConnectivityObserver
import com.mango.fakestore.core.network.connectivity.ConnectivityStatus
import com.mango.fakestore.core.security.integrity.IntegrityChecker
import com.mango.fakestore.core.security.integrity.IntegrityResult
import com.mango.fakestore.features.auth.domain.usecase.ObtenerSesionActiva
import com.mango.fakestore.features.favorites.api.ObservarConteoFavoritos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
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
    private val telemetry: Telemetry,
    private val eventTracker: EventTracker,
    private val errorMapper: DomainErrorToUiErrorMapper,
    private val integrityChecker: IntegrityChecker,
    obtenerSesionActiva: ObtenerSesionActiva,
    observarConteoFavoritos: ObservarConteoFavoritos,
) : ViewModel() {

    private val emisionErrorHandler = CoroutineExceptionHandler { _, t ->
        telemetry.reportarNoFatal(DomainError.Unknown(t))
    }

    val integridadResultado: IntegrityResult by lazy { integrityChecker.verificarIntegridad() }

    val isOffline: StateFlow<Boolean> = connectivityObserver.statusFlow
        .map { it != ConnectivityStatus.Connected }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val startDestination: StateFlow<AppRoute> = obtenerSesionActiva()
        .map { userId -> if (userId != null) AppRoute.Productos else AppRoute.Login }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppRoute.Login,
        )

    val contadorFavoritos: StateFlow<Int> = observarConteoFavoritos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )

    private val _uiEffect = MutableSharedFlow<AppUiEffect>()
    val uiEffect: SharedFlow<AppUiEffect> = _uiEffect.asSharedFlow()

    fun reportarErrorGlobal(throwable: Throwable) {
        val domainError = DomainError.Unknown(throwable)
        telemetry.reportarNoFatal(domainError)
        val uiError = errorMapper.map(domainError)
        viewModelScope.launch(emisionErrorHandler) {
            _uiEffect.emit(AppUiEffect.MostrarErrorGlobal(uiError))
        }
    }
}
