package com.mango.fakestore.core.network.reporter

import com.mango.fakestore.core.error.DomainError

interface NetworkErrorReporter {
    fun reportNetworkError(
        error: DomainError.Network,
        context: Map<String, String> = emptyMap()
    )
}

class NoOpNetworkErrorReporter : NetworkErrorReporter {
    override fun reportNetworkError(error: DomainError.Network, context: Map<String, String>) = Unit
}
