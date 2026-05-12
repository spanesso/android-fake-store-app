package com.mango.fakestore.features.products.presentation.ui.state

import com.mango.fakestore.features.products.presentation.model.ProductoUi

sealed interface ProductosUiEvent {
    data object Retry : ProductosUiEvent
    data object Refrescar : ProductosUiEvent
    data class ToggleFavorito(val producto: ProductoUi) : ProductosUiEvent
}
