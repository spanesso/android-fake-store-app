package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mango.fakestore.core.designsystem.theme.MangoColors
import com.mango.fakestore.core.designsystem.theme.MangoTheme

enum class MangoLoadingVariant { Circular, Linear, Shimmer }

@Composable
fun MangoLoadingIndicator(
    variant: MangoLoadingVariant = MangoLoadingVariant.Circular,
    modifier: Modifier = Modifier,
) {
    when (variant) {
        MangoLoadingVariant.Circular -> CircularProgressIndicator(
            modifier = modifier,
            color = MaterialTheme.colorScheme.primary,
        )
        MangoLoadingVariant.Linear -> LinearProgressIndicator(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
        )
        MangoLoadingVariant.Shimmer -> Box(
            modifier = modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(MangoColors.neutroArena),
        )
    }
}

@Preview(name = "Loading Circular - Claro", showBackground = true)
@Composable private fun LoadingCircularPreview() { MangoTheme { MangoLoadingIndicator(MangoLoadingVariant.Circular) } }

@Preview(name = "Loading Linear - Claro", showBackground = true)
@Composable private fun LoadingLinearPreview() { MangoTheme { MangoLoadingIndicator(MangoLoadingVariant.Linear) } }

@Preview(name = "Loading Shimmer - Claro", showBackground = true)
@Composable private fun LoadingShimmerPreview() { MangoTheme { MangoLoadingIndicator(MangoLoadingVariant.Shimmer) } }

@Preview(name = "Loading Circular - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun LoadingCircularDarkPreview() { MangoTheme { MangoLoadingIndicator(MangoLoadingVariant.Circular) } }
