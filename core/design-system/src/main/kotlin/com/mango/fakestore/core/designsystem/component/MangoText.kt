package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun MangoText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style ?: MaterialTheme.typography.bodyMedium,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun MangoLabel(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) = MangoText(text, modifier, style, color, maxLines, overflow)

@Preview(name = "MangoText - Claro", showBackground = true)
@Preview(name = "MangoText - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MangoTextPreview() {
    MangoTheme {
        MangoText(text = "Texto de ejemplo Mango")
    }
}
