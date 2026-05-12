package com.mango.fakestore.core.network.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val statusFlow: Flow<ConnectivityStatus>
    fun currentStatus(): ConnectivityStatus
}
