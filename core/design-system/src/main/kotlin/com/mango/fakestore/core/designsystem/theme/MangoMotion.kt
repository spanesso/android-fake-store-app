package com.mango.fakestore.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing

object MangoMotion {
    val standardEasing   = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val emphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    const val durationFast   = 150
    const val durationMedium = 300
    const val durationSlow   = 500
}
