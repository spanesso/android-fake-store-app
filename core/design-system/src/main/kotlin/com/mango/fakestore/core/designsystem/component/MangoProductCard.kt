package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mango.fakestore.core.designsystem.theme.MangoColorTokens
import com.mango.fakestore.core.designsystem.theme.MangoColors
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTextStyles
import com.mango.fakestore.core.designsystem.theme.MangoTheme

private const val CARD_ASPECT_RATIO = 0.75f

@Composable
fun MangoProductCard(
    data: MangoProductCardData,
    modifier: Modifier = Modifier,
    esFavorito: Boolean = false,
    onFavoritoClick: (() -> Unit)? = null,
    isLoading: Boolean = false,
) {
    ElevatedCard(modifier = modifier.padding(MangoSpacing.sm)) {
        if (isLoading) {
            ProductCardShimmer()
        } else {
            ProductCardImage(data = data, esFavorito = esFavorito, onFavoritoClick = onFavoritoClick)
            ProductCardInfo(data = data)
        }
    }
}

@Composable
private fun ProductCardShimmer() {
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(CARD_ASPECT_RATIO),
    ) {
        MangoLoadingIndicator(
            MangoLoadingVariant.Shimmer,
            Modifier
                .fillMaxWidth()
                .height(200.dp),
        )
    }
    Column(modifier = Modifier.padding(MangoSpacing.sm)) {
        MangoLoadingIndicator(MangoLoadingVariant.Shimmer)
        Spacer(Modifier.height(MangoSpacing.xs))
        MangoLoadingIndicator(MangoLoadingVariant.Shimmer, Modifier.fillMaxWidth(0.5f))
    }
}

@Composable
private fun ProductCardImage(
    data: MangoProductCardData,
    esFavorito: Boolean,
    onFavoritoClick: (() -> Unit)?,
) {
    Box {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data.imagenUrl.ifBlank { null })
                .crossfade(true)
                .build(),
            contentDescription = data.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(CARD_ASPECT_RATIO),
        )
        if (onFavoritoClick != null) {
            MangoIconButton(
                imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (esFavorito) "Quitar de favoritos" else "Añadir a favoritos",
                onClick = onFavoritoClick,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun ProductCardInfo(data: MangoProductCardData) {
    Column(modifier = Modifier.padding(MangoSpacing.sm)) {
        if (data.categoria.isNotBlank()) {
            MangoText(
                text = data.categoria.uppercase(),
                style = MangoTextStyles.labelSmall,
                color = MangoColorTokens.onSurfaceVariant,
            )
            Spacer(Modifier.height(MangoSpacing.xs))
        }

        MangoText(
            text = data.title,
            style = MangoTextStyles.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        if (data.descripcion.isNotBlank()) {
            Spacer(Modifier.height(MangoSpacing.xs))
            MangoText(
                text = data.descripcion,
                style = MangoTextStyles.bodySmall,
                color = MangoColorTokens.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.height(MangoSpacing.xs))
        ProductCardPriceRating(data = data)
    }
}

@Composable
private fun ProductCardPriceRating(data: MangoProductCardData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MangoText(
            text = data.price,
            style = MangoTextStyles.bodyMedium.copy(fontWeight = FontWeight.Bold),
        )
        if (data.puntuacion > 0f) {
            ProductCardRatingChip(data = data)
        }
    }
}

@Composable
private fun ProductCardRatingChip(data: MangoProductCardData) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = MangoColors.acentoOro,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(2.dp))
        MangoText(
            text = "%.1f".format(data.puntuacion),
            style = MangoTextStyles.labelSmall,
        )
        if (data.numVotaciones > 0) {
            MangoText(
                text = " (${data.numVotaciones})",
                style = MangoTextStyles.labelSmall,
                color = MangoColorTokens.onSurfaceVariant,
            )
        }
    }
}

private val previewData = MangoProductCardData(
    title = "Fjallraven Backpack",
    price = "$109.95",
    categoria = "Men's clothing",
    descripcion = "Your perfect pack for everyday use and walks in the forest.",
    puntuacion = 3.9f,
    numVotaciones = 120,
)

@Preview(name = "ProductCard Content - Claro", showBackground = true)
@Composable
private fun ProductCardContentPreview() {
    MangoTheme {
        MangoProductCard(data = previewData)
    }
}

@Preview(name = "ProductCard Loading - Claro", showBackground = true)
@Composable
private fun ProductCardShimmerPreview() {
    MangoTheme { MangoProductCard(data = MangoProductCardData("", ""), isLoading = true) }
}

@Preview(name = "ProductCard Favorito - Claro", showBackground = true)
@Composable
private fun ProductCardFavoritoPreview() {
    MangoTheme {
        MangoProductCard(
            data = MangoProductCardData(
                title = "Camiseta Lino",
                price = "49,99 €",
                categoria = "Women's",
                puntuacion = 4.5f,
                numVotaciones = 300,
            ),
            esFavorito = true,
            onFavoritoClick = {},
        )
    }
}

@Preview(name = "ProductCard - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductCardDarkPreview() {
    MangoTheme {
        MangoProductCard(
            data = MangoProductCardData(
                title = "Camiseta Lino",
                price = "49,99 €",
                puntuacion = 4.1f,
                numVotaciones = 259,
            ),
        )
    }
}
