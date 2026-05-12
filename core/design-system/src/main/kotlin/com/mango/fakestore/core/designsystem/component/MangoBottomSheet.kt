package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangoBottomSheet(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        content()
    }
}

@Preview(name = "BottomSheet - Claro", showBackground = true)
@Composable private fun BottomSheetPreview() {
    MangoTheme {
        MangoText("Vista previa de BottomSheet", Modifier.padding(MangoSpacing.md))
    }
}
