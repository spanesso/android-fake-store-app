package com.mango.fakestore.features.favorites.presentation.ui.state

import com.mango.fakestore.core.error.UiError

sealed interface FavoritosUiEffect {
    data class MostrarSnackbar(val uiError: UiError) : FavoritosUiEffect
}
