package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTheme

enum class MangoCardVariant { Elevated, Filled, Outlined }

@Composable
fun MangoCard(
    modifier: Modifier = Modifier,
    variant: MangoCardVariant = MangoCardVariant.Elevated,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (variant) {
        MangoCardVariant.Elevated -> ElevatedCard(modifier = modifier) {
            Column(Modifier.padding(MangoSpacing.md)) { content() }
        }
        MangoCardVariant.Filled -> Card(modifier = modifier) {
            Column(Modifier.padding(MangoSpacing.md)) { content() }
        }
        MangoCardVariant.Outlined -> OutlinedCard(modifier = modifier) {
            Column(Modifier.padding(MangoSpacing.md)) { content() }
        }
    }
}

@Preview(name = "Card Elevated - Claro", showBackground = true)
@Composable private fun CardElevatedPreview() { MangoTheme { MangoCard { MangoText("Tarjeta Elevated") } } }

@Preview(name = "Card Outlined - Claro", showBackground = true)
@Composable private fun CardOutlinedPreview() { MangoTheme { MangoCard(variant = MangoCardVariant.Outlined) { MangoText("Tarjeta Outlined") } } }

@Preview(name = "Card - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun CardDarkPreview() { MangoTheme { MangoCard { MangoText("Tarjeta oscura") } } }
