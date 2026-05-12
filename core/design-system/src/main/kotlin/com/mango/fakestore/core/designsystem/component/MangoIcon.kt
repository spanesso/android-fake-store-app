package com.mango.fakestore.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun MangoIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

@Preview(name = "MangoIcon - Claro", showBackground = true)
@Preview(name = "MangoIcon - Oscuro", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MangoIconPreview() {
    MangoTheme {
        MangoIcon(imageVector = Icons.Default.Favorite, contentDescription = "Favorito")
    }
}
