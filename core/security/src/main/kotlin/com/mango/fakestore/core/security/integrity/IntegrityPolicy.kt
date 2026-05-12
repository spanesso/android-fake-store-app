package com.mango.fakestore.core.security.integrity

/**
 * Política de respuesta ante una detección de entorno comprometido.
 *
 * Configurada por flavor mediante BuildConfig.INTEGRITY_POLICY:
 *   dev     → LOG   (no interrumpe el desarrollo en emuladores)
 *   staging → WARN  (avisa al equipo de QA sin bloquear)
 *   prod    → BLOCK (protección máxima para usuarios finales)
 */
enum class IntegrityPolicy {
    /** Bloquea la app — muestra diálogo sin opción de continuar. */
    BLOCK,

    /** Advierte al usuario pero permite continuar bajo su responsabilidad. */
    WARN,

    /** Solo registra en telemetría — no interrumpe el flujo. */
    LOG,
}
