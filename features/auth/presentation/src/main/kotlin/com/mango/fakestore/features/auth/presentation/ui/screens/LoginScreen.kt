package com.mango.fakestore.features.auth.presentation.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.component.MangoCard
import com.mango.fakestore.core.designsystem.component.MangoErrorState
import com.mango.fakestore.core.designsystem.component.MangoLoadingIndicator
import com.mango.fakestore.core.designsystem.component.MangoText
import com.mango.fakestore.core.designsystem.component.MangoTopAppBar
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTextStyles
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.features.auth.presentation.model.UsuarioSeleccionUi
import com.mango.fakestore.features.auth.presentation.state.LoginUiEvent
import com.mango.fakestore.features.auth.presentation.state.LoginUiState
import com.mango.fakestore.core.error.R as ErrorR

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEvent: (LoginUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        MangoTopAppBar(title = "Selecciona tu usuario")

        when (uiState) {
            is LoginUiState.Idle -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(MangoSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(MangoSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(MangoSpacing.sm),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(uiState.usuarios) { usuario ->
                        UsuarioCard(
                            usuario = usuario,
                            onClick = { onEvent(LoginUiEvent.SeleccionarUsuario(usuario.id)) },
                        )
                    }
                }
            }

            is LoginUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    MangoLoadingIndicator()
                }
            }

            is LoginUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    MangoErrorState(
                        uiError = uiState.uiError,
                        onRetry = { onEvent(LoginUiEvent.Retry) },
                    )
                }
            }
        }
    }
}

@Composable
private fun UsuarioCard(
    usuario: UsuarioSeleccionUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MangoCard(
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MangoSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MangoSpacing.xs),
        ) {
            MangoText(
                text = "${usuario.id}",
                style = MangoTextStyles.headlineLarge,
            )
            MangoText(
                text = usuario.etiqueta,
                style = MangoTextStyles.bodyMedium,
            )
        }
    }
}

// region Previews

private val listaUsuarios = (1..10).map { UsuarioSeleccionUi(id = it, etiqueta = "Usuario $it") }

private val errorNoConexion = UiError(
    messageRes = ErrorR.string.error_red_sin_conexion,
    severity = UiError.Severity.Blocking,
    actions = listOf(UiError.UiErrorAction.Retry),
    errorCode = "NET-000",
)

@Preview(name = "Login Idle - Claro", showBackground = true)
@Composable
private fun LoginIdleClaroPreview() {
    MangoTheme { LoginScreen(uiState = LoginUiState.Idle(listaUsuarios), onEvent = {}) }
}

@Preview(name = "Login Loading - Claro", showBackground = true)
@Composable
private fun LoginLoadingPreview() {
    MangoTheme { LoginScreen(uiState = LoginUiState.Loading(3), onEvent = {}) }
}

@Preview(name = "Login Error - Claro", showBackground = true)
@Composable
private fun LoginErrorPreview() {
    MangoTheme { LoginScreen(uiState = LoginUiState.Error(errorNoConexion), onEvent = {}) }
}

@Preview(name = "Login Idle - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginIdleOscuroPreview() {
    MangoTheme { LoginScreen(uiState = LoginUiState.Idle(listaUsuarios), onEvent = {}) }
}

// endregion
