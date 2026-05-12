package com.mango.fakestore.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object MangoColors {
    val neutroBlanco = Color(0xFFFFFFFF)
    val neutroHueso  = Color(0xFFF5F1EC)
    val neutroArena  = Color(0xFFE6DED3)
    val neutroPiedra = Color(0xFFB8AEA2)
    val neutroGrafito= Color(0xFF2B2B2B)
    val neutroNegro  = Color(0xFF0A0A0A)

    val acentoOro    = Color(0xFFB08D57)
    val acentoRojo   = Color(0xFF8B1E1E)

    val semanticoExito  = Color(0xFF2E7D32)
    val semanticoError  = Color(0xFFB00020)
    val semanticoAviso  = Color(0xFFB26500)

    val lightScheme = lightColorScheme(
        primary          = acentoOro,
        onPrimary        = neutroBlanco,
        secondary        = neutroGrafito,
        onSecondary      = neutroBlanco,
        background       = neutroHueso,
        onBackground     = neutroNegro,
        surface          = neutroBlanco,
        onSurface        = neutroNegro,
        error            = semanticoError,
        onError          = neutroBlanco,
    )

    val darkScheme = darkColorScheme(
        primary          = acentoOro,
        onPrimary        = neutroNegro,
        secondary        = neutroPiedra,
        onSecondary      = neutroNegro,
        background       = neutroNegro,
        onBackground     = neutroHueso,
        surface          = neutroGrafito,
        onSurface        = neutroHueso,
        error            = semanticoError,
        onError          = neutroBlanco,
    )
}
