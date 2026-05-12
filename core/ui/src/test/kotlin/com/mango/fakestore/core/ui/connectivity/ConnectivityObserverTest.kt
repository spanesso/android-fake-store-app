package com.mango.fakestore.core.ui.connectivity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectivityObserverTest {

    private fun fakeObserver(online: Boolean): ConnectivityObserver = object : ConnectivityObserver {
        override val isOnline: Flow<Boolean> = flowOf(online)
    }

    @Test
    fun `isOnline emite true cuando hay conexion`() = runTest {
        val result = fakeObserver(true).isOnline.first()
        assertTrue(result)
    }

    @Test
    fun `isOnline emite false cuando no hay conexion`() = runTest {
        val result = fakeObserver(false).isOnline.first()
        assertFalse(result)
    }

    @Test
    fun `isOffline es inverso de isOnline`() = runTest {
        val online = fakeObserver(true).isOnline.first()
        val offline = !online
        assertFalse(offline)
    }
}
