package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoColors
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun MangoOfflineBannerContent(
    isOffline: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(visible = isOffline, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MangoColors.semanticoAviso)
                .padding(horizontal = MangoSpacing.md, vertical = MangoSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MangoText("Sin conexión a internet", color = MangoColors.neutroBlanco)
        }
    }
}

@Preview(name = "OfflineBanner Visible - Claro", showBackground = true)
@Composable private fun OfflineBannerVisiblePreview() { MangoTheme { MangoOfflineBannerContent(isOffline = true) } }

@Preview(name = "OfflineBanner Oculto - Claro", showBackground = true)
@Composable private fun OfflineBannerHiddenPreview() { MangoTheme { MangoOfflineBannerContent(isOffline = false) } }

@Preview(name = "OfflineBanner - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun OfflineBannerDarkPreview() { MangoTheme { MangoOfflineBannerContent(isOffline = true) } }
