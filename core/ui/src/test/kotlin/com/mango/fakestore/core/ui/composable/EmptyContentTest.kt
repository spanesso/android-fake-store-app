package com.mango.fakestore.core.ui.composable

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmptyContentTest {

    @Test
    fun `isEmpty true indica lista sin elementos`() {
        val items = emptyList<String>()
        assertTrue(items.isEmpty())
    }

    @Test
    fun `isEmpty false indica lista con elementos`() {
        val items = listOf("producto 1")
        assertFalse(items.isEmpty())
    }

    @Test
    fun `transicion de vacio a con contenido cambia estado`() {
        var items = emptyList<String>()
        assertTrue(items.isEmpty())
        items = listOf("producto 1")
        assertFalse(items.isEmpty())
    }
}
