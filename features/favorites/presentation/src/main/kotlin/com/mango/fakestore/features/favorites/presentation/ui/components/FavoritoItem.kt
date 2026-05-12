package com.mango.fakestore.features.favorites.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mango.fakestore.core.designsystem.component.MangoIconButton
import com.mango.fakestore.core.designsystem.component.MangoProductCard
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.features.favorites.presentation.R
import com.mango.fakestore.features.favorites.presentation.model.FavoritoUi
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiEvent

@Composable
fun FavoritoItem(
    favorito: FavoritoUi,
    onEvent: (FavoritosUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        MangoProductCard(
            title = favorito.titulo,
            price = "€${String.format("%.2f", favorito.precio)}",
            modifier = Modifier.fillMaxWidth(),
        )
        MangoIconButton(
            imageVector = Icons.Filled.Favorite,
            contentDescription = stringResource(R.string.favoritos_quitar_descripcion),
            onClick = { onEvent(FavoritosUiEvent.ToggleFavorito(favorito)) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp),
        )
    }
}

@Preview(name = "FavoritoItem - Claro", showBackground = true)
@Composable
private fun FavoritoItemClaroPreview() {
    MangoTheme {
        FavoritoItem(
            favorito = FavoritoUi(
                productoId = 1,
                titulo = "Camiseta de lino",
                precio = 49.99,
                imagenUrl = "https://fakestoreapi.com/img/1.jpg",
                categoria = "ropa",
            ),
            onEvent = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(name = "FavoritoItem - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FavoritoItemOscuroPreview() {
    MangoTheme {
        FavoritoItem(
            favorito = FavoritoUi(
                productoId = 1,
                titulo = "Camiseta de lino",
                precio = 49.99,
                imagenUrl = "https://fakestoreapi.com/img/1.jpg",
                categoria = "ropa",
            ),
            onEvent = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
