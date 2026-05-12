package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoTheme

enum class MangoTextFieldVariant { Outlined, Filled, Underlined }

@Composable
fun MangoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    variant: MangoTextFieldVariant = MangoTextFieldVariant.Outlined,
    isError: Boolean = false,
    supportingText: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        leadingIcon = leadingIcon?.let { { MangoIcon(it, null) } },
        trailingIcon = trailingIcon?.let { { MangoIcon(it, null) } },
        enabled = enabled,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
    )
}

@Preview(name = "TextField Idle - Claro", showBackground = true)
@Composable private fun TfIdlePreview() {
    MangoTheme { MangoTextField(value = "", onValueChange = {}, label = "Correo") }
}

@Preview(name = "TextField Error - Claro", showBackground = true)
@Composable private fun TfErrorPreview() {
    MangoTheme { MangoTextField(value = "invalid", onValueChange = {}, label = "Correo", isError = true, supportingText = "Formato incorrecto") }
}

@Preview(name = "TextField Disabled - Claro", showBackground = true)
@Composable private fun TfDisabledPreview() {
    MangoTheme { MangoTextField(value = "disabled", onValueChange = {}, enabled = false) }
}

@Preview(name = "TextField - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun TfDarkPreview() {
    MangoTheme { MangoTextField(value = "", onValueChange = {}, label = "Correo") }
}
