package com.mango.fakestore.core.network.config

data class NetworkConfig(
    val baseUrl: String,
    val connectTimeoutSeconds: Long = 10L,
    val readTimeoutSeconds: Long = 30L,
    val writeTimeoutSeconds: Long = 30L,
    val maxRetries: Int = 3,
    val retryBaseDelayMs: Long = 500L,
    val retryMaxDelayMs: Long = 10_000L,
    val certificatePins: List<String> = listOf(
        "sha256/dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=",
        // Backup pin — placeholder. Actualizar con:
        // openssl s_client -connect fakestoreapi.com:443 </dev/null \
        //   | openssl x509 -pubkey -noout | openssl pkey -pubin -outform DER \
        //   | openssl dgst -sha256 -binary | openssl enc -base64
        "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
    )
)
