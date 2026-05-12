package com.mango.fakestore.features.products.presentation.ui.screens

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
import com.mango.fakestore.features.products.presentation.R
import com.mango.fakestore.features.products.presentation.model.ProductoUi
import com.mango.fakestore.features.products.presentation.ui.components.ProductoItem
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiEvent
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiState
import com.mango.fakestore.core.error.R as ErrorR

@Composable
fun ProductosScreen(
    uiState: ProductosUiState,
    onEvent: (ProductosUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is ProductosUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MangoLoadingIndicator()
            }
        }

        is ProductosUiState.Content -> {
            Column(modifier = modifier.fillMaxSize()) {
                MangoTopAppBar(title = stringResource(R.string.productos_titulo_pantalla))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(
                        items = uiState.productos,
                        key = { producto -> producto.id },
                    ) { producto ->
                        ProductoItem(
                            producto = producto,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        is ProductosUiState.Empty -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MangoEmptyState(
                    message = stringResource(R.string.productos_lista_vacia),
                )
            }
        }

        is ProductosUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MangoErrorState(
                    uiError = uiState.error,
                    onRetry = { onEvent(ProductosUiEvent.Retry) },
                )
            }
        }
    }
}

// region Previews

private val sampleProductos = listOf(
    ProductoUi(
        id = 1,
        titulo = "Camiseta de lino",
        precio = "$49.99",
        categoria = "ropa",
        imagenUrl = "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
        puntuacion = 4.1f,
        numVotaciones = 259,
    ),
    ProductoUi(
        id = 2,
        titulo = "Pantalon slim fit",
        precio = "$79.99",
        categoria = "ropa",
        imagenUrl = "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg",
        puntuacion = 3.9f,
        numVotaciones = 120,
    ),
    ProductoUi(
        id = 3,
        titulo = "Bolso de cuero",
        precio = "$129.99",
        categoria = "accesorios",
        imagenUrl = "https://fakestoreapi.com/img/81fAZal24fL._AC_UY879_.jpg",
        puntuacion = 4.5f,
        numVotaciones = 400,
    ),
    ProductoUi(
        id = 4,
        titulo = "Vestido floral",
        precio = "$89.99",
        categoria = "ropa",
        imagenUrl = "https://fakestoreapi.com/img/51UDEzMJVpL._AC_UL640_FMwebp_QL65_.jpg",
        puntuacion = 4.3f,
        numVotaciones = 187,
    ),
)

private val sampleUiError = UiError(
    messageRes = ErrorR.string.error_red_sin_conexion,
    severity = UiError.Severity.Blocking,
    actions = listOf(UiError.UiErrorAction.Retry),
    errorCode = "NET-000",
)

@Preview(name = "ProductosScreenLoading - Claro", showBackground = true)
@Composable
private fun ProductosScreenLoadingClaroPreview() {
    MangoTheme {
        ProductosScreen(
            uiState = ProductosUiState.Loading,
            onEvent = {},
        )
    }
}

@Preview(name = "ProductosScreenLoading - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductosScreenLoadingOscuroPreview() {
    MangoTheme {
        ProductosScreen(
            uiState = ProductosUiState.Loading,
            onEvent = {},
        )
    }
}

@Preview(name = "ProductosScreenContent - Claro", showBackground = true)
@Composable
private fun ProductosScreenContentClaroPreview() {
    MangoTheme {
        ProductosScreen(
            uiState = ProductosUiState.Content(sampleProductos),
            onEvent = {},
        )
    }
}

@Preview(name = "ProductosScreenContent - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductosScreenContentOscuroPreview() {
    MangoTheme {
        ProductosScreen(
            uiState = ProductosUiState.Content(sampleProductos),
            onEvent = {},
        )
    }
}

@Preview(name = "ProductosScreenEmpty - Claro", showBackground = true)
@Composable
private fun ProductosScreenEmptyClaroPreview() {
    MangoTheme {
        ProductosScreen(
            uiState = ProductosUiState.Empty,
            onEvent = {},
        )
    }
}

@Preview(name = "ProductosScreenEmpty - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductosScreenEmptyOscuroPreview() {
    MangoTheme {
        ProductosScreen(
            uiState = ProductosUiState.Empty,
            onEvent = {},
        )
    }
}

@Preview(name = "ProductosScreenError - Claro", showBackground = true)
@Composable
private fun ProductosScreenErrorClaroPreview() {
    MangoTheme {
        ProductosScreen(
            uiState = ProductosUiState.Error(sampleUiError),
            onEvent = {},
        )
    }
}

@Preview(name = "ProductosScreenError - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductosScreenErrorOscuroPreview() {
    MangoTheme {
        ProductosScreen(
            uiState = ProductosUiState.Error(sampleUiError),
            onEvent = {},
        )
    }
}

// endregion
