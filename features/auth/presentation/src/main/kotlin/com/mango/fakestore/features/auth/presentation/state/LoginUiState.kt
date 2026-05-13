package com.mango.fakestore.features.auth.presentation.state

import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.features.auth.presentation.model.UsuarioSeleccionUi

sealed interface LoginUiState {
    data class Idle(val usuarios: List<UsuarioSeleccionUi>) : LoginUiState
    data class Loading(val usuarioId: Int) : LoginUiState
    data class Error(val uiError: UiError) : LoginUiState
}
