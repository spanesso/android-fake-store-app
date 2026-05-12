package com.mango.fakestore.core.security

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.security.integrity.IntegrityPolicy
import org.junit.Test

class IntegrityPolicyTest {

    @Test
    fun `valueOf BLOCK devuelve BLOCK`() {
        assertThat(IntegrityPolicy.valueOf("BLOCK")).isEqualTo(IntegrityPolicy.BLOCK)
    }

    @Test
    fun `valueOf WARN devuelve WARN`() {
        assertThat(IntegrityPolicy.valueOf("WARN")).isEqualTo(IntegrityPolicy.WARN)
    }

    @Test
    fun `valueOf LOG devuelve LOG`() {
        assertThat(IntegrityPolicy.valueOf("LOG")).isEqualTo(IntegrityPolicy.LOG)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf con valor invalido lanza IllegalArgumentException`() {
        IntegrityPolicy.valueOf("INVALIDO")
    }

    @Test
    fun `enum tiene exactamente tres valores`() {
        assertThat(IntegrityPolicy.entries).hasSize(3)
    }
}
