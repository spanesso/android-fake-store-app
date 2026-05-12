package com.mango.fakestore.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
