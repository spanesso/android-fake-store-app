package com.mango.fakestore.core.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Claro", showBackground = true)
@Preview(name = "Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class PreviewLightDark

@Preview(name = "Fuente normal", showBackground = true, fontScale = 1.0f)
@Preview(name = "Fuente grande", showBackground = true, fontScale = 1.5f)
annotation class PreviewFontScale

@Preview(name = "Claro", showBackground = true)
@Preview(name = "Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Fuente grande", showBackground = true, fontScale = 1.5f)
annotation class MangoPreview
