package com.mango.fakestore.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.mango.fakestore.core.designsystem.theme.MangoColors
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTheme

private val TITULO_TAMANO = 14.sp
private val TITULO_ESPACIO_LETRAS = 0.18.em
private val DIVISOR_GROSOR = 1.dp

@Composable
fun MangoTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MangoSpacing.md, vertical = MangoSpacing.sm),
        ) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                navigationIcon()
            }
            MangoText(
                text = title.uppercase(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = TITULO_TAMANO,
                    letterSpacing = TITULO_ESPACIO_LETRAS,
                    fontWeight = FontWeight.Normal,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center),
            )
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                actions()
            }
        }
        HorizontalDivider(
            color = MangoColors.neutroArena,
            thickness = DIVISOR_GROSOR,
        )
    }
}

@Preview(name = "TopAppBar - Claro", showBackground = true)
@Composable private fun TopBarPreview() {
    MangoTheme { MangoTopAppBar("Tienda") }
}

@Preview(name = "TopAppBar - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun TopBarDarkPreview() {
    MangoTheme { MangoTopAppBar("Tienda") }
}

@Preview(name = "TopAppBar - Con acciones", showBackground = true)
@Composable private fun TopBarActionsPreview() {
    MangoTheme {
        MangoTopAppBar(
            title = "Favoritos",
            actions = { MangoText("×", style = MaterialTheme.typography.titleLarge) },
        )
    }
}
