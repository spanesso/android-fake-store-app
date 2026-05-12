package com.mango.fakestore.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Composable accessors for Mango typography tokens.
 * Feature modules should use these instead of importing MaterialTheme directly.
 */
object MangoTextStyles {
    val displayLarge: TextStyle @Composable get() = MaterialTheme.typography.displayLarge
    val headlineLarge: TextStyle @Composable get() = MaterialTheme.typography.headlineLarge
    val headlineMedium: TextStyle @Composable get() = MaterialTheme.typography.headlineMedium
    val headlineSmall: TextStyle @Composable get() = MaterialTheme.typography.headlineSmall
    val titleLarge: TextStyle @Composable get() = MaterialTheme.typography.titleLarge
    val titleMedium: TextStyle @Composable get() = MaterialTheme.typography.titleMedium
    val bodyLarge: TextStyle @Composable get() = MaterialTheme.typography.bodyLarge
    val bodyMedium: TextStyle @Composable get() = MaterialTheme.typography.bodyMedium
    val bodySmall: TextStyle @Composable get() = MaterialTheme.typography.bodySmall
    val labelLarge: TextStyle @Composable get() = MaterialTheme.typography.labelLarge
    val labelMedium: TextStyle @Composable get() = MaterialTheme.typography.labelMedium
    val labelSmall: TextStyle @Composable get() = MaterialTheme.typography.labelSmall
}

/**
 * Composable accessors for Mango semantic color tokens.
 * Feature modules should use these instead of importing MaterialTheme directly.
 */
object MangoColorTokens {
    val primary: Color @Composable get() = MaterialTheme.colorScheme.primary
    val onPrimary: Color @Composable get() = MaterialTheme.colorScheme.onPrimary
    val surface: Color @Composable get() = MaterialTheme.colorScheme.surface
    val onSurface: Color @Composable get() = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val background: Color @Composable get() = MaterialTheme.colorScheme.background
    val onBackground: Color @Composable get() = MaterialTheme.colorScheme.onBackground
    val error: Color @Composable get() = MaterialTheme.colorScheme.error
    val onError: Color @Composable get() = MaterialTheme.colorScheme.onError
}

fun buildMangoTypography(): Typography {
    val headline = TypographyConfig.headlineFontFamily
    val body = TypographyConfig.bodyFontFamily
    return Typography(
        displayLarge   = TextStyle(fontFamily = headline, fontSize = 57.sp, lineHeight = 64.sp),
        headlineLarge  = TextStyle(fontFamily = headline, fontSize = 45.sp, lineHeight = 52.sp),
        headlineMedium = TextStyle(fontFamily = headline, fontSize = 36.sp, lineHeight = 44.sp),
        headlineSmall  = TextStyle(fontFamily = headline, fontSize = 28.sp, lineHeight = 36.sp),
        titleLarge     = TextStyle(fontFamily = body, fontSize = 22.sp, lineHeight = 28.sp),
        titleMedium    = TextStyle(fontFamily = body, fontSize = 16.sp, fontWeight = FontWeight.Medium),
        bodyLarge      = TextStyle(fontFamily = body, fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium     = TextStyle(fontFamily = body, fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall      = TextStyle(fontFamily = body, fontSize = 12.sp, lineHeight = 16.sp),
        labelLarge     = TextStyle(fontFamily = body, fontSize = 11.sp, fontWeight = FontWeight.Medium),
        labelSmall     = TextStyle(fontFamily = body, fontSize = 10.sp, lineHeight = 14.sp),
    )
}
