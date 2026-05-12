package com.mango.fakestore.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object MangoShapes {
    val none = RoundedCornerShape(0.dp)
    val sm   = RoundedCornerShape(4.dp)
    val md   = RoundedCornerShape(8.dp)
    val lg   = RoundedCornerShape(16.dp)
    val pill = RoundedCornerShape(50)
}

fun buildMangoShapes() = Shapes(
    extraSmall = MangoShapes.sm,
    small      = MangoShapes.sm,
    medium     = MangoShapes.md,
    large      = MangoShapes.lg,
    extraLarge = MangoShapes.pill,
)
