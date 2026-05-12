package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun MangoDialog(
    title: String,
    text: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissLabel: String? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { MangoText(title) },
        text = { MangoText(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) { MangoText(confirmLabel) }
        },
        dismissButton = dismissLabel?.let {
            { TextButton(onClick = onDismiss) { MangoText(it) } }
        },
    )
}

@Preview(name = "Dialog - Claro", showBackground = true)
@Composable private fun DialogPreview() {
    MangoTheme { MangoDialog("Confirmar", "¿Seguro que quieres continuar?", "Sí", {}, {}, "No") }
}

@Preview(name = "Dialog - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun DialogDarkPreview() {
    MangoTheme { MangoDialog("Confirmar", "¿Seguro?", "Sí", {}, {}, "No") }
}
