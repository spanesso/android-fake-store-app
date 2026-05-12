package com.mango.fakestore.features.products.presentation.ui.state

sealed interface ProductosUiEvent {
    data object Retry : ProductosUiEvent
    data object Refrescar : ProductosUiEvent
}
