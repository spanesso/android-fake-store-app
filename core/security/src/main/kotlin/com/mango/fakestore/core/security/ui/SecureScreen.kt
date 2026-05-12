package com.mango.fakestore.core.security.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView

@Composable
fun SecureScreen(contenido: @Composable () -> Unit) {
    val vista = LocalView.current
    if (!LocalInspectionMode.current) {
        DisposableEffect(Unit) {
            val ventana = (vista.context as? Activity)?.window
            ventana?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            onDispose {
                ventana?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }
    contenido()
}
