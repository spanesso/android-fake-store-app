package com.mango.fakestore.features.products.presentation.ui.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mango.fakestore.features.products.presentation.ui.screens.ProductosScreen
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiEffect
import com.mango.fakestore.features.products.presentation.viewmodel.ProductosViewModel

@Composable
fun ProductosRoute(
    viewModel: ProductosViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // TODO: Conectar snackbarHostState a Scaffold en ProductosScreen para mostrar efectos
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ProductosUiEffect.MostrarSnackbar -> {
                    // TODO: snackbarHostState.showSnackbar(message = context.getString(effect.error.messageRes))
                }
            }
        }
    }

    ProductosScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
