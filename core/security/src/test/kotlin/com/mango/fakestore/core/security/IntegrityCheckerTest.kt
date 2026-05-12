package com.mango.fakestore.core.security

import android.content.Context
import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.security.integrity.IntegrityChecker
import io.mockk.mockk
import org.junit.Test

class IntegrityCheckerTest {

    private val contexto = mockk<Context>(relaxed = true)

    @Test
    fun `FakeIntegrityChecker comprometido retorna true`() {
        val checker = FakeIntegrityChecker(comprometido = true)
        assertThat(checker.estaComprometido()).isTrue()
    }

    @Test
    fun `FakeIntegrityChecker no comprometido retorna false`() {
        val checker = FakeIntegrityChecker(comprometido = false)
        assertThat(checker.estaComprometido()).isFalse()
    }

    private class FakeIntegrityChecker(private val comprometido: Boolean) : IntegrityChecker {
        override fun estaComprometido() = comprometido
    }
}
