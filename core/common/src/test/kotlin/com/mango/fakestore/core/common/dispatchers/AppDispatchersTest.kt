package com.mango.fakestore.core.common.dispatchers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppDispatchersTest {

    @Test
    fun `DefaultAppDispatchers expone los cuatro dispatchers no nulos`() {
        val dispatchers = DefaultAppDispatchers()
        assertNotNull(dispatchers.io)
        assertNotNull(dispatchers.main)
        assertNotNull(dispatchers.default)
        assertNotNull(dispatchers.unconfined)
    }

    @Test
    fun `AppDispatchers puede reemplazarse con TestDispatcher en tests`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testDispatchers = object : AppDispatchers {
            override val io = testDispatcher
            override val main = testDispatcher
            override val default = testDispatcher
            override val unconfined = testDispatcher
        }
        assertNotNull(testDispatchers.io)
        assertNotNull(testDispatchers.main)
    }
}
