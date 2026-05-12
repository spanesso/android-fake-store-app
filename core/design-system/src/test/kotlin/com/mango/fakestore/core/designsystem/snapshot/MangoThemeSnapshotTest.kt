package com.mango.fakestore.core.designsystem.snapshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.mango.fakestore.core.designsystem.component.MangoButton
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import org.junit.Rule
import org.junit.Test

class MangoButtonSnapshotTest {
    @get:Rule val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test fun button_primary_idle_light() = paparazzi.snapshot {
        MangoTheme(darkTheme = false) { MangoButton("Confirmar", {}) }
    }

    @Test fun button_primary_loading_light() = paparazzi.snapshot {
        MangoTheme(darkTheme = false) {
            MangoButton("Confirmar", {}, state = com.mango.fakestore.core.designsystem.component.MangoButtonState.Loading)
        }
    }

    @Test fun button_primary_disabled_light() = paparazzi.snapshot {
        MangoTheme(darkTheme = false) {
            MangoButton("Confirmar", {}, state = com.mango.fakestore.core.designsystem.component.MangoButtonState.Disabled)
        }
    }

    @Test fun button_primary_idle_dark() = paparazzi.snapshot {
        MangoTheme(darkTheme = true) { MangoButton("Confirmar", {}) }
    }
}
