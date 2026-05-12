package com.mango.fakestore.core.testing.dispatchers

import com.mango.fakestore.core.common.dispatchers.AppDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler

class TestAppDispatchers(
    scheduler: TestCoroutineScheduler = TestCoroutineScheduler(),
    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher(scheduler),
) : AppDispatchers {
    override val io: CoroutineDispatcher get() = testDispatcher
    override val main: CoroutineDispatcher get() = testDispatcher
    override val default: CoroutineDispatcher get() = testDispatcher
    override val unconfined: CoroutineDispatcher get() = testDispatcher
}
