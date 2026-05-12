package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mango.fakestore.core.designsystem.theme.MangoColors
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun MangoProductCard(
    title: String,
    price: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    MangoCard(modifier = modifier) {
        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(120.dp).background(MangoColors.neutroArena))
            Spacer(Modifier.height(MangoSpacing.sm))
            MangoLoadingIndicator(MangoLoadingVariant.Shimmer)
            Spacer(Modifier.height(MangoSpacing.xs))
            MangoLoadingIndicator(MangoLoadingVariant.Shimmer, Modifier.fillMaxWidth(0.5f))
        } else {
            Box(Modifier.fillMaxWidth().height(120.dp).background(MangoColors.neutroArena))
            Spacer(Modifier.height(MangoSpacing.sm))
            MangoText(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(MangoSpacing.xs))
            MangoText(text = price, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(name = "ProductCard Idle - Claro", showBackground = true)
@Composable private fun ProductCardPreview() { MangoTheme { MangoProductCard("Camiseta Lino", "49,99 €") } }

@Preview(name = "ProductCard Loading - Claro", showBackground = true)
@Composable private fun ProductCardShimmerPreview() { MangoTheme { MangoProductCard("", "", isLoading = true) } }

@Preview(name = "ProductCard - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun ProductCardDarkPreview() { MangoTheme { MangoProductCard("Camiseta Lino", "49,99 €") } }
