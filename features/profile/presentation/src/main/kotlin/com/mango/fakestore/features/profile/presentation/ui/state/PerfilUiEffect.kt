package com.mango.fakestore.features.profile.presentation.ui.state

import com.mango.fakestore.core.error.UiError

sealed interface PerfilUiEffect {
    data class MostrarSnackbar(val error: UiError) : PerfilUiEffect
    data object NavLogin : PerfilUiEffect
}
