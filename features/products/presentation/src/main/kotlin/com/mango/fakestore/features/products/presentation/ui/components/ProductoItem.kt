package com.mango.fakestore.features.products.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.component.MangoProductCard
import com.mango.fakestore.core.designsystem.component.MangoProductCardData
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.features.products.presentation.model.ProductoUi
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiEvent

@Composable
fun ProductoItem(
    producto: ProductoUi,
    onEvent: (ProductosUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    MangoProductCard(
        data = MangoProductCardData(
            title = producto.titulo,
            price = producto.precio,
            imagenUrl = producto.imagenUrl,
            categoria = producto.categoria,
            puntuacion = producto.puntuacion,
            numVotaciones = producto.numVotaciones,
        ),
        esFavorito = producto.esFavorito,
        onFavoritoClick = { onEvent(ProductosUiEvent.ToggleFavorito(producto)) },
        modifier = modifier.fillMaxWidth(),
    )
}

@Preview(name = "ProductoItem - Claro", showBackground = true)
@Composable
private fun ProductoItemClaroPreview() {
    MangoTheme {
        ProductoItem(
            producto = ProductoUi(
                id = 1,
                titulo = "Camiseta de lino",
                precio = "$49.99",
                precioDouble = 49.99,
                categoria = "ropa",
                imagenUrl = "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
                puntuacion = 4.1f,
                numVotaciones = 259,
                esFavorito = false,
            ),
            onEvent = {},
        )
    }
}

@Preview(name = "ProductoItem - Claro Favorito", showBackground = true)
@Composable
private fun ProductoItemClaroFavoritoPreview() {
    MangoTheme {
        ProductoItem(
            producto = ProductoUi(
                id = 2,
                titulo = "Pantalon slim fit",
                precio = "$79.99",
                precioDouble = 79.99,
                categoria = "ropa",
                imagenUrl = "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg",
                puntuacion = 3.9f,
                numVotaciones = 120,
                esFavorito = true,
            ),
            onEvent = {},
        )
    }
}

@Preview(name = "ProductoItem - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductoItemOscuroPreview() {
    MangoTheme {
        ProductoItem(
            producto = ProductoUi(
                id = 3,
                titulo = "Bolso de cuero",
                precio = "$129.99",
                precioDouble = 129.99,
                categoria = "accesorios",
                imagenUrl = "https://fakestoreapi.com/img/81fAZal24fL._AC_UY879_.jpg",
                puntuacion = 4.5f,
                numVotaciones = 400,
                esFavorito = false,
            ),
            onEvent = {},
        )
    }
}

@Preview(name = "ProductoItem - Oscuro Favorito", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductoItemOscuroFavoritoPreview() {
    MangoTheme {
        ProductoItem(
            producto = ProductoUi(
                id = 4,
                titulo = "Vestido floral",
                precio = "$89.99",
                precioDouble = 89.99,
                categoria = "ropa",
                imagenUrl = "https://fakestoreapi.com/img/51UDEzMJVpL._AC_UL640_FMwebp_QL65_.jpg",
                puntuacion = 4.3f,
                numVotaciones = 187,
                esFavorito = true,
            ),
            onEvent = {},
        )
    }
}
