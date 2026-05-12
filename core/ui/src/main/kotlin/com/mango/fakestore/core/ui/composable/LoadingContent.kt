package com.mango.fakestore.core.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.component.MangoLoadingIndicator
import com.mango.fakestore.core.designsystem.component.MangoLoadingVariant
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun LoadingContent(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    variant: MangoLoadingVariant = MangoLoadingVariant.Circular,
    content: @Composable () -> Unit,
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            MangoLoadingIndicator(variant = variant)
        }
    } else {
        content()
    }
}

@Preview(name = "LoadingContent cargando - Claro", showBackground = true)
@Composable
private fun LoadingContentLoadingPreview() {
    MangoTheme { LoadingContent(isLoading = true) {} }
}

@Preview(name = "LoadingContent listo - Claro", showBackground = true)
@Composable
private fun LoadingContentReadyPreview() {
    MangoTheme { LoadingContent(isLoading = false) {} }
}

@Preview(name = "LoadingContent cargando - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoadingContentDarkPreview() {
    MangoTheme { LoadingContent(isLoading = true) {} }
}
