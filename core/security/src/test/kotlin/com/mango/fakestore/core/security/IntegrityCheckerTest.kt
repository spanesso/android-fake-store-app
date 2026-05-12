package com.mango.fakestore.core.security

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.security.integrity.IntegrityChecker
import com.mango.fakestore.core.security.integrity.IntegrityPolicy
import com.mango.fakestore.core.security.integrity.IntegrityResult
import org.junit.Test

class IntegrityCheckerTest {

    @Test
    fun `FakeIntegrityChecker comprometido retorna estaComprometido true`() {
        val resultado = IntegrityResult(
            estaComprometido = true,
            razones = listOf("root_detectado"),
            politica = IntegrityPolicy.BLOCK,
        )
        val checker = FakeIntegrityChecker(resultado)
        assertThat(checker.verificarIntegridad().estaComprometido).isTrue()
    }

    @Test
    fun `FakeIntegrityChecker no comprometido retorna razones vacias`() {
        val checker = FakeIntegrityChecker(IntegrityResult.INTEGRA)
        val resultado = checker.verificarIntegridad()
        assertThat(resultado.estaComprometido).isFalse()
        assertThat(resultado.razones).isEmpty()
    }

    @Test
    fun `estaComprometido delega a verificarIntegridad`() {
        val checker = FakeIntegrityChecker(IntegrityResult.INTEGRA)
        assertThat(checker.estaComprometido()).isFalse()
    }

    @Test
    fun `estaComprometido devuelve true cuando verificarIntegridad esta comprometido`() {
        val resultado = IntegrityResult(true, listOf("frida_detectado"), IntegrityPolicy.WARN)
        val checker = FakeIntegrityChecker(resultado)
        assertThat(checker.estaComprometido()).isTrue()
    }

    @Test
    fun `IntegrityResult INTEGRA tiene valores por defecto correctos`() {
        assertThat(IntegrityResult.INTEGRA.estaComprometido).isFalse()
        assertThat(IntegrityResult.INTEGRA.razones).isEmpty()
        assertThat(IntegrityResult.INTEGRA.politica).isEqualTo(IntegrityPolicy.LOG)
    }

    private class FakeIntegrityChecker(
        private val resultado: IntegrityResult,
    ) : IntegrityChecker {
        override fun verificarIntegridad() = resultado
    }
}
