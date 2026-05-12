package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoTheme

enum class MangoTopAppBarVariant { Small, CenterAligned }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangoTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    variant: MangoTopAppBarVariant = MangoTopAppBarVariant.CenterAligned,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable () -> Unit = {},
) {
    when (variant) {
        MangoTopAppBarVariant.CenterAligned -> CenterAlignedTopAppBar(
            title = { MangoText(title) },
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = { actions() },
        )
        MangoTopAppBarVariant.Small -> TopAppBar(
            title = { MangoText(title) },
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = { actions() },
        )
    }
}

@Preview(name = "TopAppBar - Claro", showBackground = true)
@Composable private fun TopBarPreview() { MangoTheme { MangoTopAppBar("Tienda") } }

@Preview(name = "TopAppBar - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun TopBarDarkPreview() { MangoTheme { MangoTopAppBar("Tienda") } }
