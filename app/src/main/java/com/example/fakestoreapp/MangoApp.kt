package com.example.fakestoreapp

import android.app.Application
import android.os.Build
import com.mango.fakestore.core.analytics.SessionIdProvider
import com.mango.fakestore.core.analytics.Telemetry
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MangoApp : Application() {

    @Inject lateinit var telemetry: Telemetry
    @Inject lateinit var sessionIdProvider: SessionIdProvider

    override fun onCreate() {
        super.onCreate()
        initSessionContext()
    }

    private fun initSessionContext() {
        telemetry.setContexto(
            mapOf(
                "flavor" to BuildConfig.FLAVOR,
                "appVersion" to BuildConfig.VERSION_NAME,
                "buildNumber" to BuildConfig.VERSION_CODE.toString(),
                "device" to "${Build.MANUFACTURER} ${Build.MODEL}",
                "sessionId" to sessionIdProvider.obtener(),
            )
        )
    }
}
