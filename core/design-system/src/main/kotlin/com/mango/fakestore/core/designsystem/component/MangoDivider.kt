package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mango.fakestore.core.designsystem.theme.MangoColors
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun MangoDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = MangoColors.neutroArena,
    )
}

@Preview(name = "MangoDivider - Claro", showBackground = true)
@Preview(name = "MangoDivider - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MangoDividerPreview() {
    MangoTheme { MangoDivider() }
}
