package com.mango.fakestore.core.analytics.impl

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.mango.fakestore.core.analytics.ErrorRateLimiter
import com.mango.fakestore.core.analytics.RandomSessionIdProvider
import com.mango.fakestore.core.analytics.SessionIdProvider
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.logging.Logger
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class FirebaseTelemetryImplTest {

    private val crashlytics: FirebaseCrashlytics = mockk(relaxed = true)
    private val analytics: FirebaseAnalytics = mockk(relaxed = true)
    private val performance: FirebasePerformance = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)
    private val sessionIdProvider: SessionIdProvider = RandomSessionIdProvider()
    private val rateLimiter = ErrorRateLimiter()

    private lateinit var telemetry: FirebaseTelemetryImpl

    @Before
    fun setup() {
        val traza: Trace = mockk(relaxed = true)
        every { performance.newTrace(any()) } returns traza
        telemetry = FirebaseTelemetryImpl(
            crashlytics = crashlytics,
            analytics = analytics,
            performance = performance,
            logger = logger,
            sessionIdProvider = sessionIdProvider,
            rateLimiter = rateLimiter,
        )
    }

    @Test
    fun `Validation no llama a recordException`() {
        telemetry.reportarNoFatal(DomainError.Validation(mapOf("campo" to "requerido")))
        verify(exactly = 0) { crashlytics.recordException(any()) }
    }

    @Test
    fun `BiometricLockout no llama a recordException`() {
        telemetry.reportarNoFatal(DomainError.Security.BiometricLockout)
        verify(exactly = 0) { crashlytics.recordException(any()) }
    }

    @Test
    fun `error de red llama a recordException`() {
        telemetry.reportarNoFatal(DomainError.Network.NoConnection())
        verify(exactly = 1) { crashlytics.recordException(any()) }
    }

    @Test
    fun `error de red configura tags estandar en Crashlytics`() {
        telemetry.reportarNoFatal(
            DomainError.Network.Timeout(),
            contexto = mapOf("module" to "products", "useCase" to "ObtenerProductos"),
        )
        verify { crashlytics.setCustomKey("errorType", any<String>()) }
        verify { crashlytics.setCustomKey("sessionId", any<String>()) }
        verify { crashlytics.setCustomKey("module", "products") }
    }

    @Test
    fun `rate-limiter bloquea el reporte mas alla del limite`() {
        val limiter = ErrorRateLimiter(maxPorVentana = 2)
        val t = FirebaseTelemetryImpl(
            crashlytics, analytics, performance, logger, sessionIdProvider, limiter,
        )
        t.reportarNoFatal(DomainError.Network.Timeout())
        t.reportarNoFatal(DomainError.Network.Timeout())
        t.reportarNoFatal(DomainError.Network.Timeout()) // bloqueado
        verify(exactly = 2) { crashlytics.recordException(any()) }
    }

    @Test
    fun `setUserId delega a crashlytics setUserId`() {
        telemetry.setUserId("abc123hash")
        verify { crashlytics.setUserId("abc123hash") }
    }

    @Test
    fun `setContexto delega cada clave a crashlytics setCustomKey`() {
        telemetry.setContexto(mapOf("flavor" to "dev", "appVersion" to "1.0.0"))
        verify { crashlytics.setCustomKey("flavor", "dev") }
        verify { crashlytics.setCustomKey("appVersion", "1.0.0") }
    }

    @Test
    fun `Network Server incluye httpCode en los tags`() {
        telemetry.reportarNoFatal(DomainError.Network.Server(httpCode = 503))
        verify { crashlytics.setCustomKey("httpCode", "503") }
    }
}
