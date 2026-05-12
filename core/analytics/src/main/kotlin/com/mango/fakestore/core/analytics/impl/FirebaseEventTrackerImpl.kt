package com.mango.fakestore.core.analytics.impl

import com.mango.fakestore.core.analytics.AnalyticsEvent
import com.mango.fakestore.core.analytics.EventTracker
import com.mango.fakestore.core.analytics.Telemetry

class FirebaseEventTrackerImpl(private val telemetry: Telemetry) : EventTracker {
    override fun registrar(evento: AnalyticsEvent) {
        telemetry.registrarEvento(evento.nombre, evento.params)
    }
}
