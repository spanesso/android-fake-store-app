package com.mango.fakestore.core.network.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ConnectivityObserverImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val statusFlow: Flow<ConnectivityStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                if (caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    trySend(ConnectivityStatus.Connected)
                }
            }

            override fun onLost(network: Network) {
                trySend(ConnectivityStatus.Disconnected)
            }

            override fun onUnavailable() {
                trySend(ConnectivityStatus.Unavailable)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }

    override fun currentStatus(): ConnectivityStatus {
        val network = connectivityManager.activeNetwork ?: return ConnectivityStatus.Unavailable
        val caps = connectivityManager.getNetworkCapabilities(network)
            ?: return ConnectivityStatus.Unavailable
        return if (caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            ConnectivityStatus.Connected
        } else {
            ConnectivityStatus.Unavailable
        }
    }
}
