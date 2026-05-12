package com.mango.fakestore.core.network.connectivity

sealed interface ConnectivityStatus {
    data object Connected : ConnectivityStatus
    data object Disconnected : ConnectivityStatus
    data object Unavailable : ConnectivityStatus
}
