package com.mango.fakestore.features.favorites.presentation.ui.state

import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.features.favorites.presentation.model.FavoritoUi

sealed interface FavoritosUiState {
    object Loading : FavoritosUiState
    data class Content(val favoritos: List<FavoritoUi>) : FavoritosUiState
    object Empty : FavoritosUiState
    data class Error(val uiError: UiError) : FavoritosUiState
}
