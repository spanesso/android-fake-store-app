package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.error.R
import com.mango.fakestore.core.error.UiError

@Composable
fun MangoErrorState(
    uiError: UiError,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.padding(MangoSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        MangoText(stringResource(uiError.messageRes))
        if (onRetry != null) {
            Spacer(Modifier.height(MangoSpacing.md))
            MangoButton("Reintentar", onClick = onRetry)
        }
    }
}

@Preview(name = "ErrorState con Retry - Claro", showBackground = true)
@Composable private fun ErrorStateWithRetryPreview() {
    MangoTheme {
        MangoErrorState(
            uiError = UiError(R.string.error_red_sin_conexion, UiError.Severity.Blocking, listOf(UiError.UiErrorAction.Retry), "NET-000"),
            onRetry = {},
        )
    }
}

@Preview(name = "ErrorState sin Retry - Claro", showBackground = true)
@Composable private fun ErrorStateNoRetryPreview() {
    MangoTheme {
        MangoErrorState(
            uiError = UiError(R.string.error_red_no_autorizado, UiError.Severity.Fatal, listOf(UiError.UiErrorAction.Login), "NET-401"),
        )
    }
}

@Preview(name = "ErrorState - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun ErrorStateDarkPreview() {
    MangoTheme {
        MangoErrorState(
            uiError = UiError(R.string.error_desconocido, UiError.Severity.Blocking, emptyList(), "UNK-000"),
            onRetry = {},
        )
    }
}
