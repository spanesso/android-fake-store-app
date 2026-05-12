package com.mango.fakestore.features.favorites.presentation.ui.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mango.fakestore.features.favorites.presentation.ui.screens.FavoritosScreen
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiEffect
import com.mango.fakestore.features.favorites.presentation.viewmodel.FavoritosViewModel

@Composable
fun FavoritosRoute(
    modifier: Modifier = Modifier,
    viewModel: FavoritosViewModel = hiltViewModel(),
    onMostrarSnackbar: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { efecto ->
            when (efecto) {
                is FavoritosUiEffect.MostrarSnackbar -> onMostrarSnackbar(efecto.uiError.errorCode)
            }
        }
    }

    FavoritosScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
