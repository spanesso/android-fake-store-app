package com.mango.fakestore.core.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mango.fakestore.core.designsystem.component.MangoOfflineBannerContent
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.ui.connectivity.ConnectivityObserver
import com.mango.fakestore.core.ui.connectivity.DefaultConnectivityObserver
import kotlinx.coroutines.flow.map

@Composable
fun MangoOfflineBanner(
    modifier: Modifier = Modifier,
    connectivityObserver: ConnectivityObserver = rememberConnectivityObserver(),
) {
    val isOfflineFlow = remember(connectivityObserver) {
        connectivityObserver.isOnline.map { !it }
    }
    val isOffline by isOfflineFlow.collectAsStateWithLifecycle(initialValue = false)

    MangoOfflineBannerContent(isOffline = isOffline, modifier = modifier)
}

@Composable
private fun rememberConnectivityObserver(): ConnectivityObserver {
    val context = LocalContext.current
    return remember { DefaultConnectivityObserver(context) }
}

@Preview(name = "OfflineBanner Stateful - Claro", showBackground = true)
@Composable
private fun MangoOfflineBannerPreview() {
    MangoTheme { MangoOfflineBannerContent(isOffline = true) }
}

@Preview(name = "OfflineBanner Stateful - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MangoOfflineBannerDarkPreview() {
    MangoTheme { MangoOfflineBannerContent(isOffline = true) }
}
