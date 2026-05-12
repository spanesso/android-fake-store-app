package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.InputChip
import androidx.compose.material3.SuggestionChip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoTheme

enum class MangoChipType { Filter, Assist, Input, Suggestion }

@Composable
fun MangoChip(
    label: String,
    type: MangoChipType = MangoChipType.Filter,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    when (type) {
        MangoChipType.Filter -> FilterChip(
            selected = selected, onClick = onClick,
            label = { MangoText(label) }, enabled = enabled, modifier = modifier,
        )
        MangoChipType.Assist -> AssistChip(
            onClick = onClick, label = { MangoText(label) }, enabled = enabled, modifier = modifier,
        )
        MangoChipType.Input -> InputChip(
            selected = selected, onClick = onClick,
            label = { MangoText(label) }, enabled = enabled, modifier = modifier,
        )
        MangoChipType.Suggestion -> SuggestionChip(
            onClick = onClick, label = { MangoText(label) }, enabled = enabled, modifier = modifier,
        )
    }
}

@Preview(name = "Chip Filter Idle - Claro", showBackground = true)
@Composable private fun ChipFilterPreview() { MangoTheme { MangoChip("Talla S", MangoChipType.Filter) } }

@Preview(name = "Chip Filter Selected - Claro", showBackground = true)
@Composable private fun ChipFilterSelectedPreview() { MangoTheme { MangoChip("Talla S", MangoChipType.Filter, selected = true) } }

@Preview(name = "Chip - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun ChipDarkPreview() { MangoTheme { MangoChip("Talla M", MangoChipType.Filter, selected = true) } }
