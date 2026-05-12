package com.mango.fakestore.features.favorites.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.component.MangoProductCard
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.features.favorites.presentation.model.FavoritoUi
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiEvent
import java.util.Locale

@Composable
fun FavoritoItem(
    favorito: FavoritoUi,
    onEvent: (FavoritosUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    MangoProductCard(
        title = favorito.titulo,
        price = "€${String.format(Locale.getDefault(), "%.2f", favorito.precio)}",
        imagenUrl = favorito.imagenUrl,
        categoria = favorito.categoria,
        esFavorito = true,
        onFavoritoClick = { onEvent(FavoritosUiEvent.ToggleFavorito(favorito)) },
        modifier = modifier.fillMaxWidth(),
    )
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
