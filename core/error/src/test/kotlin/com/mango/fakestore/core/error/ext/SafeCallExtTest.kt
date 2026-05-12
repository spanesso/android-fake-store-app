package com.mango.fakestore.core.error.ext

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class SafeCallExtTest {

    @Test
    fun `safeApiCall retorna Right en happy path`() = runTest {
        val result = safeApiCall { "success" }
        assertTrue(result is Either.Right)
    }

    @Test
    fun `safeApiCall retorna Left con DomainError para IOException`() = runTest {
        val result = safeApiCall<String> { throw IOException("red caida") }
        assertTrue(result is Either.Left)
        assertTrue((result as Either.Left).value is DomainError.Network)
    }

    @Test
    @Suppress("SwallowedException")
    fun `safeApiCall propaga CancellationException sin envolver`() = runTest {
        var propagated = false
        try {
            safeApiCall<String> { throw CancellationException("cancelled") }
        } catch (e: CancellationException) {
            propagated = true
        }
        assertTrue(propagated)
    }

    @Test
    fun `safeDbCall retorna Right en happy path`() = runTest {
        val result = safeDbCall { 42 }
        assertTrue(result is Either.Right)
    }

    @Test
    @Suppress("SwallowedException")
    fun `safeDbCall propaga CancellationException sin envolver`() = runTest {
        var propagated = false
        try {
            safeDbCall<Int> { throw CancellationException("cancelled") }
        } catch (e: CancellationException) {
            propagated = true
        }
        assertTrue(propagated)
    }
}
