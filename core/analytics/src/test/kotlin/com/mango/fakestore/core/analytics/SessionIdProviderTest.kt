package com.mango.fakestore.core.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SessionIdProviderTest {

    @Test
    fun `mismo ID en llamadas repetidas de la misma instancia`() {
        val provider = RandomSessionIdProvider()
        val id1 = provider.obtener()
        val id2 = provider.obtener()
        assertEquals(id1, id2)
    }

    @Test
    fun `instancias distintas generan IDs distintos`() {
        val idA = RandomSessionIdProvider().obtener()
        val idB = RandomSessionIdProvider().obtener()
        assertNotEquals(idA, idB)
    }

    @Test
    fun `el ID no es nulo ni vacio`() {
        val id = RandomSessionIdProvider().obtener()
        assertNotNull(id)
        assert(id.isNotBlank())
    }

    @Test
    fun `el ID tiene formato UUID`() {
        val id = RandomSessionIdProvider().obtener()
        // UUID v4: 8-4-4-4-12 hex chars con guiones
        val uuidRegex = Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        assert(uuidRegex.matches(id)) { "El ID no tiene formato UUID: $id" }
    }
}
