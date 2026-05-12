package com.mango.fakestore.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MangoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) MangoColors.darkScheme else MangoColors.lightScheme,
        typography  = buildMangoTypography(),
        shapes      = buildMangoShapes(),
        content     = content,
    )
}
