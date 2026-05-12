package com.example.fakestoreapp.ui

import com.mango.fakestore.core.error.UiError

sealed interface AppUiEffect {
    data class MostrarErrorGlobal(val uiError: UiError) : AppUiEffect
}
