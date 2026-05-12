package com.mango.fakestore.features.auth.presentation.state

sealed interface LoginUiEvent {
    data class SeleccionarUsuario(val id: Int) : LoginUiEvent
    data object Retry : LoginUiEvent
}
