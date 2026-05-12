package com.mango.fakestore.features.favorites.presentation.ui.state

import com.mango.fakestore.features.favorites.presentation.model.FavoritoUi

sealed interface FavoritosUiEvent {
    object Cargar : FavoritosUiEvent
    data class ToggleFavorito(val favorito: FavoritoUi) : FavoritosUiEvent
    object Reintentar : FavoritosUiEvent
}
