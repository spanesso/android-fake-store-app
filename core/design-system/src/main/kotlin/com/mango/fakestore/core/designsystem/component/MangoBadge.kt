package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.theme.MangoTheme

@Composable
fun MangoBadge(
    count: Int? = null,
    modifier: Modifier = Modifier,
) {
    Badge(modifier = modifier) {
        if (count != null) {
            MangoText(text = count.toString(), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(name = "MangoBadge con número - Claro", showBackground = true)
@Composable
private fun MangoBadgeWithCountPreview() {
    MangoTheme { MangoBadge(count = 5) }
}

@Preview(name = "MangoBadge punto - Claro", showBackground = true)
@Composable
private fun MangoBadgeDotPreview() {
    MangoTheme { MangoBadge() }
}

@Preview(name = "MangoBadge - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MangoBadgeDarkPreview() {
    MangoTheme { MangoBadge(count = 3) }
}
