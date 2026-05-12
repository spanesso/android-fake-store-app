package com.mango.fakestore.core.common.ext

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowEitherExtTest {

    @Test
    fun `mapEitherRight transforma valores Right`() = runTest {
        flowOf(1.right(), "err".left(), 2.right())
            .mapEitherRight { it * 10 }
            .test {
                assertEquals(10.right(), awaitItem())
                assertEquals("err".left(), awaitItem())
                assertEquals(20.right(), awaitItem())
                awaitComplete()
            }
    }

    @Test
    fun `filterEitherRight emite solo valores Right`() = runTest {
        flowOf(1.right(), "err".left(), 2.right())
            .filterEitherRight()
            .test {
                assertEquals(1, awaitItem())
                assertEquals(2, awaitItem())
                awaitComplete()
            }
    }
}
