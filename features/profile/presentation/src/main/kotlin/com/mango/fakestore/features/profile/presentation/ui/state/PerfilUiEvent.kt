package com.mango.fakestore.features.profile.presentation.ui.state

sealed interface PerfilUiEvent {
    data object Retry : PerfilUiEvent
}
