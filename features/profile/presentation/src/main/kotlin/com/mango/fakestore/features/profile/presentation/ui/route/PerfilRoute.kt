package com.mango.fakestore.features.profile.presentation.ui.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mango.fakestore.features.profile.presentation.ui.screens.PerfilScreen
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiEffect
import com.mango.fakestore.features.profile.presentation.viewmodel.PerfilViewModel

@Composable
fun PerfilRoute(
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = hiltViewModel(),
    onMostrarSnackbar: (String) -> Unit = {},
    onCerrarSesion: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { efecto ->
            when (efecto) {
                is PerfilUiEffect.MostrarSnackbar -> onMostrarSnackbar(efecto.error.errorCode)
                is PerfilUiEffect.NavLogin -> onCerrarSesion()
            }
        }
    }

    PerfilScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
