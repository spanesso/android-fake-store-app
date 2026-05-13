package com.mango.fakestore.features.auth.presentation.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import com.mango.fakestore.core.designsystem.component.MangoButton
import com.mango.fakestore.core.designsystem.component.MangoButtonState
import com.mango.fakestore.core.designsystem.component.MangoButtonVariant
import com.mango.fakestore.core.designsystem.component.MangoErrorState
import com.mango.fakestore.core.designsystem.component.MangoIcon
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MangoColorTokens.background),
    ) {
        when (uiState) {
            is LoginUiState.Idle -> LoginIdleContent(
                usuarios = uiState.usuarios,
                onConfirmar = { id -> onEvent(LoginUiEvent.SeleccionarUsuario(id)) },
                modifier = Modifier.fillMaxSize(),
            )

            is LoginUiState.Loading -> LoginLoadingContent(
                modifier = Modifier.fillMaxSize(),
            )

            is LoginUiState.Error -> LoginErrorContent(
                uiError = uiState.uiError,
                onRetry = { onEvent(LoginUiEvent.Retry) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun LoginIdleContent(
    usuarios: List<UsuarioSeleccionUi>,
    onConfirmar: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var seleccionadoId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier.padding(horizontal = MangoSpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(MangoSpacing.xxl))

        LoginHeader()

        Spacer(modifier = Modifier.height(MangoSpacing.lg))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MangoSpacing.xs),
        ) {
            items(usuarios, key = { it.id }) { usuario ->
                UsuarioRadioItem(
                    etiqueta = usuario.etiqueta,
                    seleccionado = seleccionadoId == usuario.id,
                    onClick = { seleccionadoId = usuario.id },
                )
            }
        }

        Spacer(modifier = Modifier.height(MangoSpacing.md))

        MangoButton(
            text = "Continuar",
            onClick = { seleccionadoId?.let(onConfirmar) },
            modifier = Modifier.fillMaxWidth(),
            state = if (seleccionadoId == null) MangoButtonState.Disabled else MangoButtonState.Idle,
        )

        Spacer(modifier = Modifier.height(MangoSpacing.xl))
    }
}

@Composable
private fun LoginHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MangoSpacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MangoColorTokens.primary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            MangoIcon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
            )
        }

        MangoText(
            text = "Selecciona tu usuario",
            style = MangoTextStyles.headlineSmall.copy(letterSpacing = 0.sp),
            color = MangoColorTokens.onBackground,
            modifier = Modifier.padding(top = MangoSpacing.xs),
        )

        MangoText(
            text = "Elige el usuario con el que deseas continuar.",
            style = MangoTextStyles.bodyMedium,
            color = MangoColorTokens.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun UsuarioRadioItem(
    etiqueta: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MangoColorTokens.primary
    val surface = MangoColorTokens.surface
    val onSurface = MangoColorTokens.onSurface
    val outline = MangoColorTokens.onSurfaceVariant.copy(alpha = 0.3f)
    val itemBackground = if (seleccionado) primary.copy(alpha = 0.08f) else surface
    val borderColor = if (seleccionado) primary.copy(alpha = 0.5f) else outline

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(itemBackground)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = MangoSpacing.md, vertical = MangoSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MangoSpacing.md),
    ) {
        RadioIndicator(selected = seleccionado, color = primary, outline = onSurface.copy(alpha = 0.4f))
        MangoText(
            text = etiqueta,
            style = MangoTextStyles.bodyLarge,
            color = if (seleccionado) primary else onSurface,
        )
    }
}

@Composable
private fun RadioIndicator(
    selected: Boolean,
    color: Color,
    outline: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .border(2.dp, if (selected) color else outline, CircleShape)
            .padding(5.dp)
            .background(if (selected) color else Color.Transparent, CircleShape),
    )
}

@Composable
private fun LoginLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(MangoSpacing.xxl))
        LoginHeader()
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            MangoLoadingIndicator()
        }
    }
}

@Composable
private fun LoginErrorContent(
    uiError: UiError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(MangoSpacing.xxl))
        LoginHeader()
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            MangoErrorState(uiError = uiError, onRetry = onRetry)
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
