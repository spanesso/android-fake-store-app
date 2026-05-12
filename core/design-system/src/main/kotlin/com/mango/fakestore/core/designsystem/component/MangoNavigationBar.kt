package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import com.mango.fakestore.core.designsystem.theme.MangoTheme

data class MangoNavItem(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean = false,
    val badgeCount: Int? = null,
    val onClick: () -> Unit = {},
)

@Composable
fun MangoNavigationBar(
    items: List<MangoNavItem>,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.selected,
                onClick = item.onClick,
                icon = { MangoIcon(item.icon, item.label) },
                label = { MangoText(item.label) },
            )
        }
    }
}

@Preview(name = "NavBar - Claro", showBackground = true)
@Composable private fun NavBarPreview() {
    MangoTheme {
        MangoNavigationBar(listOf(
            MangoNavItem("Inicio", Icons.Default.Home, selected = true),
            MangoNavItem("Favoritos", Icons.Default.Favorite),
        ))
    }
}

@Preview(name = "NavBar - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun NavBarDarkPreview() {
    MangoTheme {
        MangoNavigationBar(listOf(
            MangoNavItem("Inicio", Icons.Default.Home, selected = true),
            MangoNavItem("Favoritos", Icons.Default.Favorite),
        ))
    }
}
