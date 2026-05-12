package com.mango.fakestore.core.network.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ConnectivityObserverTest {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var observer: ConnectivityObserverImpl
    private val callbackSlot = slot<ConnectivityManager.NetworkCallback>()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        connectivityManager = mockk(relaxed = true) {
            every { registerNetworkCallback(any<NetworkRequest>(), capture(callbackSlot)) } returns Unit
        }
        val context = mockk<Context> {
            every { getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
            every { applicationContext } returns this
        }
        observer = ConnectivityObserverImpl(context)
    }

    @Test
    fun emits_connected_when_validated() = runTest(testDispatcher) {
        observer.statusFlow.test {
            callbackSlot.captured.onCapabilitiesChanged(
                mockk(),
                mockk { every { hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true },
            )
            assertThat(awaitItem()).isEqualTo(ConnectivityStatus.Connected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emits_disconnected_when_lost() = runTest(testDispatcher) {
        observer.statusFlow.test {
            callbackSlot.captured.onLost(mockk())
            assertThat(awaitItem()).isEqualTo(ConnectivityStatus.Disconnected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emits_unavailable_when_no_network() = runTest(testDispatcher) {
        observer.statusFlow.test {
            callbackSlot.captured.onUnavailable()
            assertThat(awaitItem()).isEqualTo(ConnectivityStatus.Unavailable)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun current_status_reflects_latest() = runTest(testDispatcher) {
        every { connectivityManager.activeNetwork } returns null
        every { connectivityManager.getNetworkCapabilities(null) } returns null

        assertThat(observer.currentStatus()).isEqualTo(ConnectivityStatus.Unavailable)
    }
}
