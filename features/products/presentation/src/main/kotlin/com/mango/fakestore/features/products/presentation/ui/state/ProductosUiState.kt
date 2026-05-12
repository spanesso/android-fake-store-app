package com.mango.fakestore.features.products.presentation.ui.state

import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.features.products.presentation.model.ProductoUi

sealed interface ProductosUiState {
    data object Loading : ProductosUiState
    data object Empty : ProductosUiState
    data class Error(val error: UiError) : ProductosUiState
    data class Content(val productos: List<ProductoUi>) : ProductosUiState
}
