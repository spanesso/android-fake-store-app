package com.mango.fakestore.features.auth.presentation.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mango.fakestore.core.designsystem.component.MangoCard
import com.mango.fakestore.core.designsystem.component.MangoChip
import com.mango.fakestore.core.designsystem.component.MangoChipType
import com.mango.fakestore.core.designsystem.component.MangoErrorState
import com.mango.fakestore.core.designsystem.component.MangoLoadingIndicator
import com.mango.fakestore.core.designsystem.component.MangoText
import com.mango.fakestore.core.designsystem.theme.MangoColorTokens
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MangoColorTokens.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MangoBrandHeader(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
        )

        val cardModifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = MangoSpacing.md)
            .padding(bottom = MangoSpacing.xl)

        when (uiState) {
            is LoginUiState.Idle -> LoginFormContent(
                usuarios = uiState.usuarios,
                onSelect = { onEvent(LoginUiEvent.SeleccionarUsuario(it)) },
                modifier = cardModifier,
            )

            is LoginUiState.Loading -> MangoCard(modifier = cardModifier) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    MangoLoadingIndicator()
                }
            }

            is LoginUiState.Error -> MangoCard(modifier = cardModifier) {
                MangoErrorState(
                    uiError = uiState.uiError,
                    onRetry = { onEvent(LoginUiEvent.Retry) },
                )
            }
        }
    }
}

@Composable
private fun MangoBrandHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MangoSpacing.xs),
        ) {
            MangoText(
                text = "MANGO",
                style = MangoTextStyles.headlineLarge.copy(letterSpacing = 8.sp),
                color = MangoColorTokens.onBackground,
            )
            MangoText(
                text = "Fake Store",
                style = MangoTextStyles.titleMedium,
                color = MangoColorTokens.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LoginFormContent(
    usuarios: List<UsuarioSeleccionUi>,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    MangoCard(modifier = modifier) {
        MangoText(
            text = "Iniciar sesión",
            style = MangoTextStyles.headlineSmall,
            color = MangoColorTokens.onSurface,
        )
        Spacer(modifier = Modifier.height(MangoSpacing.xxs))
        MangoText(
            text = "Selecciona tu perfil de acceso",
            style = MangoTextStyles.bodyMedium,
            color = MangoColorTokens.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(MangoSpacing.xl))
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(MangoSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(MangoSpacing.xs),
        ) {
            items(usuarios) { usuario ->
                MangoChip(
                    label = "${usuario.id}",
                    type = MangoChipType.Assist,
                    onClick = { onSelect(usuario.id) },
                )
            }
        }
        Spacer(modifier = Modifier.height(MangoSpacing.md))
        MangoText(
            text = "Toca un número para acceder",
            style = MangoTextStyles.bodySmall,
            color = MangoColorTokens.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        )
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
