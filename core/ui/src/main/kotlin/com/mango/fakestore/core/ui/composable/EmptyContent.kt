package com.mango.fakestore.core.ui.composable

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.component.MangoEmptyState
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun EmptyContent(
    isEmpty: Boolean,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    emptyTitle: String? = null,
    emptyIcon: ImageVector? = null,
    content: @Composable () -> Unit,
) {
    if (isEmpty) {
        MangoEmptyState(
            message = emptyMessage,
            modifier = modifier,
            title = emptyTitle,
            icon = emptyIcon,
        )
    } else {
        content()
    }
}

@Preview(name = "EmptyContent vacío - Claro", showBackground = true)
@Composable
private fun EmptyContentEmptyPreview() {
    MangoTheme {
        EmptyContent(isEmpty = true, emptyMessage = "No hay productos", emptyTitle = "Sin resultados") {}
    }
}

@Preview(name = "EmptyContent con contenido - Claro", showBackground = true)
@Composable
private fun EmptyContentFilledPreview() {
    MangoTheme { EmptyContent(isEmpty = false, emptyMessage = "No hay productos") {} }
}

@Preview(name = "EmptyContent vacío - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyContentDarkPreview() {
    MangoTheme { EmptyContent(isEmpty = true, emptyMessage = "No hay productos") {} }
}
