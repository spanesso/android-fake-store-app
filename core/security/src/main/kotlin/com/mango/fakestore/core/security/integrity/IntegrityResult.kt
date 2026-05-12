package com.mango.fakestore.core.security.integrity

/**
 * Resultado de la verificación de integridad del dispositivo y la app.
 *
 * Invariante: si [razones] está vacío entonces [estaComprometido] es false.
 */
data class IntegrityResult(
    /** true si cualquier check de integridad ha fallado. */
    val estaComprometido: Boolean,
    /** Lista de motivos detectados — nunca contiene PII. */
    val razones: List<String>,
    /** Política aplicada en esta verificación. */
    val politica: IntegrityPolicy,
) {
    companion object {
        /** Resultado neutro para usar en tests y fakes. */
        val INTEGRA = IntegrityResult(
            estaComprometido = false,
            razones = emptyList(),
            politica = IntegrityPolicy.LOG,
        )
    }
}
