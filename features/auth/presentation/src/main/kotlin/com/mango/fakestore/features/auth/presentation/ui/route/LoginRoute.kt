package com.mango.fakestore.features.auth.presentation.ui.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mango.fakestore.features.auth.presentation.state.LoginUiEffect
import com.mango.fakestore.features.auth.presentation.ui.screens.LoginScreen
import com.mango.fakestore.features.auth.presentation.viewmodel.LoginViewModel

@Composable
fun LoginRoute(
    onLoginExitoso: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { efecto ->
            when (efecto) {
                is LoginUiEffect.NavProductos -> onLoginExitoso()
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
