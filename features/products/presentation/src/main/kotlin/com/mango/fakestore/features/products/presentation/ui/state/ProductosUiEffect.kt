package com.mango.fakestore.features.products.presentation.ui.state

import com.mango.fakestore.core.error.UiError

sealed interface ProductosUiEffect {
    data class MostrarSnackbar(val error: UiError) : ProductosUiEffect
}
