package com.mango.fakestore.features.products.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.component.MangoProductCard
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.features.products.presentation.model.ProductoUi

@Composable
fun ProductoItem(
    producto: ProductoUi,
    modifier: Modifier = Modifier,
) {
    MangoProductCard(
        title = producto.titulo,
        price = producto.precio,
        modifier = modifier,
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
                categoria = "ropa",
                imagenUrl = "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
                puntuacion = 4.1f,
                numVotaciones = 259,
            ),
        )
    }
}

@Preview(name = "ProductoItem - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductoItemOscuroPreview() {
    MangoTheme {
        ProductoItem(
            producto = ProductoUi(
                id = 2,
                titulo = "Pantalon slim fit",
                precio = "$79.99",
                categoria = "ropa",
                imagenUrl = "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg",
                puntuacion = 3.9f,
                numVotaciones = 120,
            ),
        )
    }
}
