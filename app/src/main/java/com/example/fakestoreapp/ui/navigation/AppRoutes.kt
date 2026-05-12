package com.example.fakestoreapp.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable data object Productos : AppRoute
    @Serializable data object Favoritos : AppRoute
    @Serializable data object Perfil    : AppRoute
}
