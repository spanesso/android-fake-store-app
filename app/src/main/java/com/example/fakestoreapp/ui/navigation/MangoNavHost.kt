package com.example.fakestoreapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import com.example.fakestoreapp.R
import com.mango.fakestore.core.designsystem.component.MangoNavItem
import com.mango.fakestore.core.designsystem.component.MangoNavigationBar
import com.mango.fakestore.features.auth.presentation.ui.route.LoginRoute
import com.mango.fakestore.features.favorites.presentation.ui.route.FavoritosRoute
import com.mango.fakestore.features.products.presentation.ui.route.ProductosRoute
import com.mango.fakestore.features.profile.presentation.ui.route.PerfilRoute
import kotlinx.coroutines.launch

@Composable
fun MangoNavHost(
    navController: NavHostController,
    startDestination: AppRoute,
    onMostrarSnackbar: suspend (String) -> Unit,
    contadorFavoritos: Int,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

    val showBottomBar = !currentRoute.contains("Login")

    val itemsNav = listOf(
        MangoNavItem(
            label = context.getString(R.string.nav_productos),
            icon = Icons.Default.Home,
            selected = currentRoute.contains("Productos"),
            badgeCount = null,
            onClick = {
                navController.navigate(AppRoute.Productos) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
        ),
        MangoNavItem(
            label = context.getString(R.string.nav_favoritos),
            icon = Icons.Default.Favorite,
            selected = currentRoute.contains("Favoritos"),
            badgeCount = if (contadorFavoritos > 0) contadorFavoritos else null,
            onClick = {
                navController.navigate(AppRoute.Favoritos) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
        ),
        MangoNavItem(
            label = context.getString(R.string.nav_perfil),
            icon = Icons.Default.Person,
            selected = currentRoute.contains("Perfil"),
            badgeCount = null,
            onClick = {
                navController.navigate(AppRoute.Perfil) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
        ),
    )

    Scaffold(
        bottomBar = { if (showBottomBar) MangoNavigationBar(items = itemsNav) },
        modifier = modifier,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<AppRoute.Login> {
                LoginRoute(
                    onLoginExitoso = {
                        navController.navigate(AppRoute.Productos) {
                            popUpTo(AppRoute.Login) { inclusive = true }
                        }
                    },
                )
            }
            composable<AppRoute.Productos>(
                deepLinks = listOf(navDeepLink { uriPattern = "mango://fakestore/productos" }),
            ) {
                ProductosRoute()
            }
            composable<AppRoute.Favoritos>(
                deepLinks = listOf(navDeepLink { uriPattern = "mango://fakestore/favoritos" }),
            ) {
                FavoritosRoute(onMostrarSnackbar = { msg ->
                    coroutineScope.launch { onMostrarSnackbar(msg) }
                })
            }
            composable<AppRoute.Perfil>(
                deepLinks = listOf(navDeepLink { uriPattern = "mango://fakestore/perfil" }),
            ) {
                PerfilRoute(
                    onMostrarSnackbar = { msg ->
                        coroutineScope.launch { onMostrarSnackbar(msg) }
                    },
                    onCerrarSesion = {
                        navController.navigate(AppRoute.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}
