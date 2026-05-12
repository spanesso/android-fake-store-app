package com.mango.fakestore.core.ui.composable

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.component.MangoErrorState
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.error.R
import com.mango.fakestore.core.error.UiError

@Composable
fun ErrorContent(
    error: UiError?,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (error != null) {
        MangoErrorState(
            uiError = error,
            modifier = modifier,
            onRetry = onRetry,
        )
    } else {
        content()
    }
}

@Preview(name = "ErrorContent con error - Claro", showBackground = true)
@Composable
private fun ErrorContentWithErrorPreview() {
    MangoTheme {
        ErrorContent(
            error = UiError(
                messageRes = R.string.error_red_sin_conexion,
                severity = UiError.Severity.Blocking,
                actions = listOf(UiError.UiErrorAction.Retry),
                errorCode = "NET-000",
            ),
            onRetry = {},
        ) {}
    }
}

@Preview(name = "ErrorContent sin error - Claro", showBackground = true)
@Composable
private fun ErrorContentNoErrorPreview() {
    MangoTheme {
        ErrorContent(error = null) {}
    }
}

@Preview(name = "ErrorContent con error - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorContentDarkPreview() {
    MangoTheme {
        ErrorContent(
            error = UiError(
                messageRes = R.string.error_desconocido,
                severity = UiError.Severity.Blocking,
                actions = emptyList(),
                errorCode = "UNK-000",
            ),
            onRetry = {},
        ) {}
    }
}
