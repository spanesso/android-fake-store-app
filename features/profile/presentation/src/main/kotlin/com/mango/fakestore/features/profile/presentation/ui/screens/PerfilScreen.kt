package com.mango.fakestore.features.profile.presentation.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.component.MangoErrorState
import com.mango.fakestore.core.designsystem.component.MangoLoadingIndicator
import com.mango.fakestore.core.designsystem.component.MangoTopAppBar
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.features.profile.presentation.R
import com.mango.fakestore.features.profile.presentation.model.PerfilContenidoUi
import com.mango.fakestore.features.profile.presentation.ui.components.PerfilInfoCard
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiEvent
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiState
import com.mango.fakestore.core.error.R as ErrorR

@Composable
fun PerfilScreen(
    uiState: PerfilUiState,
    onEvent: (PerfilUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        MangoTopAppBar(title = stringResource(R.string.perfil_titulo))

        when (uiState) {
            is PerfilUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    MangoLoadingIndicator()
                }
            }

            is PerfilUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    MangoErrorState(
                        uiError = uiState.error,
                        onRetry = { onEvent(PerfilUiEvent.Retry) },
                    )
                }
            }

            is PerfilUiState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Spacer(Modifier.height(MangoSpacing.lg))
                    PerfilInfoCard(
                        usuario = uiState.usuario,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(MangoSpacing.xl))
                }
            }
        }
    }
}

// region Previews

private val sampleUsuario = PerfilContenidoUi(
    id = 8,
    nombreCompleto = "John Doe",
    nombreUsuario = "johnd",
    email = "john@example.com",
    telefono = "1-570-236-7033",
    ciudad = "kilcoole",
    calle = "new road 7835",
    codigoPostal = "12926-3874",
    contadorFavoritos = 5,
)

private val errorNoConexion = UiError(
    messageRes = ErrorR.string.error_red_sin_conexion,
    severity = UiError.Severity.Blocking,
    actions = listOf(UiError.UiErrorAction.Retry),
    errorCode = "NET-000",
)

private val errorNoEncontrado = UiError(
    messageRes = R.string.error_perfil_no_encontrado,
    severity = UiError.Severity.Info,
    actions = listOf(UiError.UiErrorAction.Retry, UiError.UiErrorAction.Dismiss),
    errorCode = "NET-404",
)

@Preview(name = "Perfil - Loading - Claro", showBackground = true)
@Composable
private fun LoadingClaroPreview() {
    MangoTheme { PerfilScreen(uiState = PerfilUiState.Loading, onEvent = {}) }
}

@Preview(name = "Perfil - Loading - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoadingOscuroPreview() {
    MangoTheme { PerfilScreen(uiState = PerfilUiState.Loading, onEvent = {}) }
}

@Preview(name = "Perfil - Error Sin Conexion - Claro", showBackground = true)
@Composable
private fun ErrorNoConexionClaroPreview() {
    MangoTheme { PerfilScreen(uiState = PerfilUiState.Error(errorNoConexion), onEvent = {}) }
}

@Preview(name = "Perfil - Error Sin Conexion - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorNoConexionOscuroPreview() {
    MangoTheme { PerfilScreen(uiState = PerfilUiState.Error(errorNoConexion), onEvent = {}) }
}

@Preview(name = "Perfil - Error No Encontrado - Claro", showBackground = true)
@Composable
private fun ErrorNoEncontradoClaroPreview() {
    MangoTheme { PerfilScreen(uiState = PerfilUiState.Error(errorNoEncontrado), onEvent = {}) }
}

@Preview(
    name = "Perfil - Error No Encontrado - Oscuro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ErrorNoEncontradoOscuroPreview() {
    MangoTheme { PerfilScreen(uiState = PerfilUiState.Error(errorNoEncontrado), onEvent = {}) }
}

@Preview(name = "Perfil - Content - Claro", showBackground = true)
@Composable
private fun ContentClaroPreview() {
    MangoTheme { PerfilScreen(uiState = PerfilUiState.Content(sampleUsuario), onEvent = {}) }
}

@Preview(name = "Perfil - Content - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ContentOscuroPreview() {
    MangoTheme { PerfilScreen(uiState = PerfilUiState.Content(sampleUsuario), onEvent = {}) }
}

@Preview(name = "Perfil - Content sin favoritos - Claro", showBackground = true)
@Composable
private fun ContentSinFavoritosClaroPreview() {
    MangoTheme {
        PerfilScreen(
            uiState = PerfilUiState.Content(sampleUsuario.copy(contadorFavoritos = 0)),
            onEvent = {},
        )
    }
}

// endregion
