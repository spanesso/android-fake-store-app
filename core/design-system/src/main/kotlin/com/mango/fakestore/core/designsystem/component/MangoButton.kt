package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mango.fakestore.core.designsystem.theme.MangoColors
import com.mango.fakestore.core.designsystem.theme.MangoTheme

enum class MangoButtonVariant { Primary, Secondary, Outline, Text, Destructive }
enum class MangoButtonSize { Small, Medium, Large }
enum class MangoButtonState { Idle, Loading, Disabled }

@Composable
fun MangoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MangoButtonVariant = MangoButtonVariant.Primary,
    size: MangoButtonSize = MangoButtonSize.Medium,
    state: MangoButtonState = MangoButtonState.Idle,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    val enabled = state != MangoButtonState.Disabled
    val minHeight = when (size) {
        MangoButtonSize.Small -> 36.dp
        MangoButtonSize.Medium -> 48.dp
        MangoButtonSize.Large -> 56.dp
    }
    val contentPadding = when (size) {
        MangoButtonSize.Small -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        MangoButtonSize.Medium -> PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        MangoButtonSize.Large -> PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    }

    val content: @Composable () -> Unit = {
        if (state == MangoButtonState.Loading) {
            MangoLoadingIndicator(MangoLoadingVariant.Circular, Modifier.size(18.dp))
        } else {
            leadingIcon?.let { MangoIcon(it, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)) }
            MangoText(text)
            trailingIcon?.let { Spacer(Modifier.width(4.dp)); MangoIcon(it, null, Modifier.size(18.dp)) }
        }
    }

    when (variant) {
        MangoButtonVariant.Outline -> OutlinedButton(
            onClick = onClick, enabled = enabled,
            modifier = modifier.defaultMinSize(minHeight = minHeight),
            contentPadding = contentPadding, content = { content() },
        )
        MangoButtonVariant.Text, MangoButtonVariant.Secondary -> TextButton(
            onClick = onClick, enabled = enabled,
            modifier = modifier.defaultMinSize(minHeight = minHeight),
            contentPadding = contentPadding, content = { content() },
        )
        MangoButtonVariant.Destructive -> Button(
            onClick = onClick, enabled = enabled,
            modifier = modifier.defaultMinSize(minHeight = minHeight),
            colors = ButtonDefaults.buttonColors(containerColor = MangoColors.semanticoError),
            contentPadding = contentPadding, content = { content() },
        )
        else -> Button(
            onClick = onClick, enabled = enabled,
            modifier = modifier.defaultMinSize(minHeight = minHeight),
            contentPadding = contentPadding, content = { content() },
        )
    }
}

@Preview(name = "Button Primary Idle - Claro", showBackground = true)
@Composable private fun BtnPrimaryIdlePreview() { MangoTheme { MangoButton("Confirmar", {}) } }

@Preview(name = "Button Primary Loading - Claro", showBackground = true)
@Composable private fun BtnPrimaryLoadingPreview() { MangoTheme { MangoButton("Confirmar", {}, state = MangoButtonState.Loading) } }

@Preview(name = "Button Primary Disabled - Claro", showBackground = true)
@Composable private fun BtnPrimaryDisabledPreview() { MangoTheme { MangoButton("Confirmar", {}, state = MangoButtonState.Disabled) } }

@Preview(name = "Button Destructive - Claro", showBackground = true)
@Composable private fun BtnDestructivePreview() { MangoTheme { MangoButton("Eliminar", {}, variant = MangoButtonVariant.Destructive) } }

@Preview(name = "Button Primary - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun BtnPrimaryDarkPreview() { MangoTheme { MangoButton("Confirmar", {}) } }
