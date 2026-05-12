package com.mango.fakestore.core.designsystem.snapshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.mango.fakestore.core.designsystem.component.MangoSnackbar
import com.mango.fakestore.core.designsystem.component.MangoSnackbarSeverity
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import org.junit.Rule
import org.junit.Test

class MangoSnackbarSnapshotTest {
    @get:Rule val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test fun snackbar_info_light() = paparazzi.snapshot {
        MangoTheme(darkTheme = false) { MangoSnackbar("Información", MangoSnackbarSeverity.Info) }
    }

    @Test fun snackbar_success_light() = paparazzi.snapshot {
        MangoTheme(darkTheme = false) { MangoSnackbar("Éxito", MangoSnackbarSeverity.Success) }
    }

    @Test fun snackbar_warning_light() = paparazzi.snapshot {
        MangoTheme(darkTheme = false) { MangoSnackbar("Advertencia", MangoSnackbarSeverity.Warning) }
    }

    @Test fun snackbar_error_light() = paparazzi.snapshot {
        MangoTheme(darkTheme = false) { MangoSnackbar("Error", MangoSnackbarSeverity.Error) }
    }

    @Test fun snackbar_error_dark() = paparazzi.snapshot {
        MangoTheme(darkTheme = true) { MangoSnackbar("Error", MangoSnackbarSeverity.Error) }
    }
}
