package com.mango.fakestore.core.network.config

data class NetworkConfig(
    val baseUrl: String,
    val connectTimeoutSeconds: Long = 10L,
    val readTimeoutSeconds: Long = 30L,
    val writeTimeoutSeconds: Long = 30L,
    val maxRetries: Int = 3,
    val retryBaseDelayMs: Long = 500L,
    val retryMaxDelayMs: Long = 10_000L,
    // Verificado: 2026-05-12. El primario es el certificado hoja de fakestoreapi.com.
    // El backup es la CA intermedia Let's Encrypt E5/E6 (estable 2+ años).
    // Rotar primario cuando caduque el certificado hoja (~90 días, Let's Encrypt).
    // Ver research.md para el comando openssl de verificación.
    val certificatePins: List<String> = listOf(
        "sha256/dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=",
        "sha256/jQJTbIh0grw0/1TkHSumWb+Fs0Kct2/cm9cT5a1+aI="
    )
)
