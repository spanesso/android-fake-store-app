package com.mango.fakestore.core.ui.modifier

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.mango.fakestore.core.designsystem.theme.MangoColors
import com.mango.fakestore.core.designsystem.theme.MangoMotion

private const val SHIMMER_TRAVEL_DISTANCE = 1000f

fun Modifier.shimmer(
    shimmerColor: Color = MangoColors.neutroArena,
    highlightColor: Color = MangoColors.neutroBlanco,
    durationMillis: Int = MangoMotion.durationMedium,
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = SHIMMER_TRAVEL_DISTANCE,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
        ),
        label = "shimmer_translate",
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(shimmerColor, highlightColor, shimmerColor),
            start = Offset(translateAnim - SHIMMER_TRAVEL_DISTANCE, 0f),
            end = Offset(translateAnim, 0f),
        ),
    )
}
