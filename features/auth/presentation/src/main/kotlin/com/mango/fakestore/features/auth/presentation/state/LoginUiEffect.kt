package com.mango.fakestore.features.auth.presentation.state

sealed interface LoginUiEffect {
    data object NavProductos : LoginUiEffect
}
