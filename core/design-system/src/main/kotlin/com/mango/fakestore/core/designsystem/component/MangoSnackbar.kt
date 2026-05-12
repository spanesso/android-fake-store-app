package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoColors
import com.mango.fakestore.core.designsystem.theme.MangoShapes
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTheme

enum class MangoSnackbarSeverity { Info, Success, Warning, Error }

@Composable
fun MangoSnackbar(
    message: String,
    severity: MangoSnackbarSeverity = MangoSnackbarSeverity.Info,
    modifier: Modifier = Modifier,
) {
    val backgroundColor: Color = when (severity) {
        MangoSnackbarSeverity.Info    -> MangoColors.neutroGrafito
        MangoSnackbarSeverity.Success -> MangoColors.semanticoExito
        MangoSnackbarSeverity.Warning -> MangoColors.semanticoAviso
        MangoSnackbarSeverity.Error   -> MangoColors.semanticoError
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MangoShapes.md)
            .background(backgroundColor)
            .padding(horizontal = MangoSpacing.md, vertical = MangoSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MangoText(message, color = MangoColors.neutroBlanco)
    }
}

@Preview(name = "Snackbar Info - Claro", showBackground = true)
@Composable private fun SnackbarInfoPreview() { MangoTheme { MangoSnackbar("Información disponible", MangoSnackbarSeverity.Info) } }

@Preview(name = "Snackbar Success - Claro", showBackground = true)
@Composable private fun SnackbarSuccessPreview() { MangoTheme { MangoSnackbar("Operación exitosa", MangoSnackbarSeverity.Success) } }

@Preview(name = "Snackbar Warning - Claro", showBackground = true)
@Composable private fun SnackbarWarningPreview() { MangoTheme { MangoSnackbar("Atención requerida", MangoSnackbarSeverity.Warning) } }

@Preview(name = "Snackbar Error - Claro", showBackground = true)
@Composable private fun SnackbarErrorPreview() { MangoTheme { MangoSnackbar("Ha ocurrido un error", MangoSnackbarSeverity.Error) } }

@Preview(name = "Snackbar - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun SnackbarDarkPreview() { MangoTheme { MangoSnackbar("Error en la red", MangoSnackbarSeverity.Error) } }
