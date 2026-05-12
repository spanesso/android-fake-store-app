package com.mango.fakestore.core.security.integrity

interface IntegrityChecker {
    /** Verifica el entorno y retorna un resultado detallado. */
    fun verificarIntegridad(): IntegrityResult

    /** Conveniencia — delega en [verificarIntegridad]. Retrocompatible con código existente. */
    fun estaComprometido(): Boolean = verificarIntegridad().estaComprometido
}
