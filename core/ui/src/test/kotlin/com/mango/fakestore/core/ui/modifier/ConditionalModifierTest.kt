package com.mango.fakestore.core.ui.modifier

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConditionalModifierTest {

    private fun applyIf(condition: Boolean, block: () -> Unit) {
        if (condition) block()
    }

    @Test
    fun `conditional con true ejecuta el bloque`() {
        var executed = false
        applyIf(true) { executed = true }
        assertTrue(executed)
    }

    @Test
    fun `conditional con false no ejecuta el bloque`() {
        var executed = false
        applyIf(false) { executed = true }
        assertFalse(executed)
    }

    @Test
    fun `conditional solo aplica cuando la condicion es verdadera`() {
        val results = mutableListOf<Boolean>()
        listOf(true, false, true, false).forEach { condition ->
            applyIf(condition) { results.add(true) }
        }
        assertTrue(results.size == 2)
        assertTrue(results.all { it })
    }
}
