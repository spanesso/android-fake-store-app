package com.mango.fakestore.core.analytics

interface EventTracker {
    fun registrar(evento: AnalyticsEvent)
}
