package com.mango.fakestore.features.favorites.presentation.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mango.fakestore.core.designsystem.component.MangoEmptyState
import com.mango.fakestore.core.designsystem.component.MangoErrorState
import com.mango.fakestore.core.designsystem.component.MangoLoadingIndicator
import com.mango.fakestore.core.designsystem.component.MangoTopAppBar
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.features.favorites.presentation.R
import com.mango.fakestore.features.favorites.presentation.model.FavoritoUi
import com.mango.fakestore.features.favorites.presentation.ui.components.FavoritoItem
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiEvent
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiState
import com.mango.fakestore.core.error.R as ErrorR

@Composable
fun FavoritosScreen(
    uiState: FavoritosUiState,
    onEvent: (FavoritosUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is FavoritosUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MangoLoadingIndicator()
            }
        }

        is FavoritosUiState.Content -> {
            Column(modifier = modifier.fillMaxSize()) {
                MangoTopAppBar(title = stringResource(R.string.favoritos_titulo_pantalla))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(
                        items = uiState.favoritos,
                        key = { favorito -> favorito.productoId },
                    ) { favorito ->
                        FavoritoItem(
                            favorito = favorito,
                            onEvent = onEvent,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        is FavoritosUiState.Empty -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MangoEmptyState(
                    message = stringResource(R.string.favoritos_lista_vacia),
                )
            }
        }

        is FavoritosUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MangoErrorState(
                    uiError = uiState.uiError,
                    onRetry = { onEvent(FavoritosUiEvent.Reintentar) },
                )
            }
        }
    }
}

// region Previews

private val sampleFavoritos = listOf(
    FavoritoUi(productoId = 1, titulo = "Camiseta de lino", precio = 49.99, imagenUrl = "", categoria = "ropa"),
    FavoritoUi(productoId = 2, titulo = "Pantalon slim fit", precio = 79.99, imagenUrl = "", categoria = "ropa"),
)

private val sampleUiError = UiError(
    messageRes = ErrorR.string.error_bd_lectura,
    severity = UiError.Severity.Blocking,
    actions = listOf(UiError.UiErrorAction.Retry),
    errorCode = "DB-001",
)

@Preview(name = "FavoritosScreenLoading - Claro", showBackground = true)
@Composable
private fun LoadingClaroPreview() {
    MangoTheme { FavoritosScreen(uiState = FavoritosUiState.Loading, onEvent = {}) }
}

@Preview(name = "FavoritosScreenLoading - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoadingOscuroPreview() {
    MangoTheme { FavoritosScreen(uiState = FavoritosUiState.Loading, onEvent = {}) }
}

@Preview(name = "FavoritosScreenContent - Claro", showBackground = true)
@Composable
private fun ContentClaroPreview() {
    MangoTheme { FavoritosScreen(uiState = FavoritosUiState.Content(sampleFavoritos), onEvent = {}) }
}

@Preview(name = "FavoritosScreenContent - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ContentOscuroPreview() {
    MangoTheme { FavoritosScreen(uiState = FavoritosUiState.Content(sampleFavoritos), onEvent = {}) }
}

@Preview(name = "FavoritosScreenEmpty - Claro", showBackground = true)
@Composable
private fun EmptyClaroPreview() {
    MangoTheme { FavoritosScreen(uiState = FavoritosUiState.Empty, onEvent = {}) }
}

@Preview(name = "FavoritosScreenEmpty - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyOscuroPreview() {
    MangoTheme { FavoritosScreen(uiState = FavoritosUiState.Empty, onEvent = {}) }
}

@Preview(name = "FavoritosScreenError - Claro", showBackground = true)
@Composable
private fun ErrorClaroPreview() {
    MangoTheme { FavoritosScreen(uiState = FavoritosUiState.Error(sampleUiError), onEvent = {}) }
}

@Preview(name = "FavoritosScreenError - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorOscuroPreview() {
    MangoTheme { FavoritosScreen(uiState = FavoritosUiState.Error(sampleUiError), onEvent = {}) }
}

// endregion
