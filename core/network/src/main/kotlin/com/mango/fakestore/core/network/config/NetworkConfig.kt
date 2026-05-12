package com.mango.fakestore.core.network.config

data class NetworkConfig(
    val baseUrl: String,
    val connectTimeoutSeconds: Long = 10L,
    val readTimeoutSeconds: Long = 30L,
    val writeTimeoutSeconds: Long = 30L,
    val maxRetries: Int = 3,
    val retryBaseDelayMs: Long = 500L,
    val retryMaxDelayMs: Long = 10_000L,
    // Verificado: 2026-05-12. Primario: certificado hoja fakestoreapi.com.
    // Backup: CA intermedia Google Trust Services WE1 (más estable).
    // Rotar primario cuando caduque el certificado hoja (~90 días).
    val certificatePins: List<String> = listOf(
        "sha256/dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=",
        "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=",
    ),
)
