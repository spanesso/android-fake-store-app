package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun MangoEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null,
) {
    Column(
        modifier = modifier.padding(MangoSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        icon?.let {
            MangoIcon(it, null)
            Spacer(Modifier.height(MangoSpacing.md))
        }
        title?.let {
            MangoText(it, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(MangoSpacing.sm))
        }
        MangoText(message, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(name = "EmptyState - Claro", showBackground = true)
@Composable private fun EmptyStatePreview() { MangoTheme { MangoEmptyState("No hay productos disponibles", title = "Sin resultados") } }

@Preview(name = "EmptyState - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun EmptyStateDarkPreview() { MangoTheme { MangoEmptyState("No hay productos") } }
