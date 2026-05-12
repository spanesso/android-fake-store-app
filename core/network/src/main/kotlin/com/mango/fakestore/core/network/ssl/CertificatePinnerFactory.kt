package com.mango.fakestore.core.network.ssl

import com.mango.fakestore.core.network.config.NetworkConfig
import okhttp3.CertificatePinner

object CertificatePinnerFactory {
    fun create(config: NetworkConfig): CertificatePinner {
        val builder = CertificatePinner.Builder()
        config.certificatePins.forEach { pin ->
            builder.add("fakestoreapi.com", pin)
        }
        return builder.build()
    }
}
