package com.mango.fakestore.features.profile.presentation.ui.state

import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.features.profile.presentation.model.PerfilContenidoUi

sealed interface PerfilUiState {
    data object Loading : PerfilUiState
    data class Error(val error: UiError) : PerfilUiState
    data class Content(val usuario: PerfilContenidoUi) : PerfilUiState
}
