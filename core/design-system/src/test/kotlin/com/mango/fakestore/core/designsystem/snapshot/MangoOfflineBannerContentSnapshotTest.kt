package com.mango.fakestore.core.designsystem.snapshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.mango.fakestore.core.designsystem.component.MangoOfflineBannerContent
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import org.junit.Rule
import org.junit.Test

class MangoOfflineBannerContentSnapshotTest {
    @get:Rule val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test fun offline_banner_visible_light() = paparazzi.snapshot {
        MangoTheme(darkTheme = false) { MangoOfflineBannerContent(isOffline = true) }
    }

    @Test fun offline_banner_hidden_light() = paparazzi.snapshot {
        MangoTheme(darkTheme = false) { MangoOfflineBannerContent(isOffline = false) }
    }

    @Test fun offline_banner_visible_dark() = paparazzi.snapshot {
        MangoTheme(darkTheme = true) { MangoOfflineBannerContent(isOffline = true) }
    }
}
