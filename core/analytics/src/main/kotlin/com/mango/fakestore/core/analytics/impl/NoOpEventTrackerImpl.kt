package com.mango.fakestore.core.analytics.impl

import com.mango.fakestore.core.analytics.AnalyticsEvent
import com.mango.fakestore.core.analytics.EventTracker

class NoOpEventTrackerImpl : EventTracker {
    override fun registrar(evento: AnalyticsEvent) = Unit
}
