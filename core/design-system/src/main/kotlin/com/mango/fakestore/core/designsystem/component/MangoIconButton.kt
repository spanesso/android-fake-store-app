package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun MangoIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(48.dp),
    ) {
        MangoIcon(imageVector = imageVector, contentDescription = contentDescription)
    }
}

@Preview(name = "IconButton Idle - Claro", showBackground = true)
@Composable private fun IconBtnIdlePreview() { MangoTheme { MangoIconButton(Icons.Default.Favorite, "Favorito", {}) } }

@Preview(name = "IconButton Disabled - Claro", showBackground = true)
@Composable private fun IconBtnDisabledPreview() { MangoTheme { MangoIconButton(Icons.Default.Favorite, "Favorito", {}, enabled = false) } }

@Preview(name = "IconButton - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun IconBtnDarkPreview() { MangoTheme { MangoIconButton(Icons.Default.Favorite, "Favorito", {}) } }
