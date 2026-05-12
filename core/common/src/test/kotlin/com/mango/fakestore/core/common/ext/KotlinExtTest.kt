package com.mango.fakestore.core.common.ext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KotlinExtTest {

    // isNotNullOrBlank

    @Test
    fun `null devuelve false para isNotNullOrBlank`() {
        val value: String? = null
        assertFalse(value.isNotNullOrBlank())
    }

    @Test
    fun `cadena vacía devuelve false para isNotNullOrBlank`() {
        assertFalse("".isNotNullOrBlank())
    }

    @Test
    fun `cadena de espacios devuelve false para isNotNullOrBlank`() {
        assertFalse("   ".isNotNullOrBlank())
    }

    @Test
    fun `cadena con contenido devuelve true para isNotNullOrBlank`() {
        assertTrue("hola".isNotNullOrBlank())
    }

    // truncate

    @Test
    fun `truncate no modifica cadena dentro del límite`() {
        assertEquals("hola", "hola".truncate(10))
    }

    @Test
    fun `truncate no modifica cadena con longitud exacta al límite`() {
        assertEquals("hola", "hola".truncate(4))
    }

    @Test
    fun `truncate recorta cadena larga con puntos suspensivos por defecto`() {
        assertEquals("ho...", "hola mundo".truncate(5))
    }

    @Test
    fun `truncate usa el ellipsis personalizado`() {
        assertEquals("ho→", "hola mundo".truncate(3, "→"))
    }

    // ifNotNull

    @Test
    fun `ifNotNull no ejecuta acción cuando valor es null`() {
        val result: Int? = null.ifNotNull<String, Int> { it.length }
        assertNull(result)
    }

    @Test
    fun `ifNotNull ejecuta acción cuando valor no es null`() {
        val result = "hola".ifNotNull { it.length }
        assertEquals(4, result)
    }

    // orDefault

    @Test
    fun `orDefault devuelve el valor cuando no es null`() {
        assertEquals("hola", "hola".orDefault("default"))
    }

    @Test
    fun `orDefault devuelve el default cuando es null`() {
        val value: String? = null
        assertEquals("default", value.orDefault("default"))
    }

    // toImmutableList

    @Test
    fun `toImmutableList preserva el orden de los elementos`() {
        val list = listOf(3, 1, 2).toImmutableList()
        assertEquals(listOf(3, 1, 2), list)
    }

    @Test
    fun `toImmutableList devuelve lista vacía para colección vacía`() {
        assertTrue(emptyList<Int>().toImmutableList().isEmpty())
    }
}
