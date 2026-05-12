package com.mango.fakestore.core.analytics

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorRateLimiterTest {

    @Test
    fun `permite reportes hasta el limite configurado`() {
        val limiter = ErrorRateLimiter(maxPorVentana = 3, ventanaMs = 60_000L)
        assertTrue(limiter.permitir("NETWORK"))
        assertTrue(limiter.permitir("NETWORK"))
        assertTrue(limiter.permitir("NETWORK"))
    }

    @Test
    fun `bloquea el reporte que supera el limite`() {
        val limiter = ErrorRateLimiter(maxPorVentana = 3, ventanaMs = 60_000L)
        repeat(3) { limiter.permitir("TIMEOUT") }
        assertFalse(limiter.permitir("TIMEOUT"))
    }

    @Test
    fun `claves independientes no se bloquean entre si`() {
        val limiter = ErrorRateLimiter(maxPorVentana = 2, ventanaMs = 60_000L)
        repeat(2) { limiter.permitir("ERROR_A") }
        assertTrue(limiter.permitir("ERROR_B"))
    }

    @Test
    fun `resetea tras la ventana de tiempo`() {
        val ventanaMs = 100L
        val limiter = ErrorRateLimiter(maxPorVentana = 1, ventanaMs = ventanaMs)
        assertTrue(limiter.permitir("TIMEOUT"))
        assertFalse(limiter.permitir("TIMEOUT"))
        Thread.sleep(ventanaMs + 10)
        assertTrue(limiter.permitir("TIMEOUT"))
    }

    @Test
    fun `primer reporte siempre permitido`() {
        val limiter = ErrorRateLimiter()
        assertTrue(limiter.permitir("cualquier_error"))
    }
}
