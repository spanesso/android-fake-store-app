package com.mango.fakestore.core.error.mapper

import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.R
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.core.error.UiError.Severity
import com.mango.fakestore.core.error.UiError.UiErrorAction

class DomainErrorToUiErrorMapper {
    fun map(error: DomainError): UiError = when (error) {
        is DomainError.Network.NoConnection -> UiError(
            messageRes = R.string.error_red_sin_conexion,
            severity = Severity.Blocking,
            actions = listOf(UiErrorAction.Retry),
            errorCode = "NET-000",
        )
        is DomainError.Network.Timeout -> UiError(
            messageRes = R.string.error_red_tiempo_agotado,
            severity = Severity.Blocking,
            actions = listOf(UiErrorAction.Retry),
            errorCode = "NET-001",
        )
        is DomainError.Network.Server -> UiError(
            messageRes = R.string.error_red_servidor,
            severity = Severity.Blocking,
            actions = listOf(UiErrorAction.Retry),
            errorCode = "NET-500",
        )
        is DomainError.Network.Unauthorized -> UiError(
            messageRes = R.string.error_red_no_autorizado,
            severity = Severity.Fatal,
            actions = listOf(UiErrorAction.Login),
            errorCode = "NET-401",
        )
        is DomainError.Network.Forbidden -> UiError(
            messageRes = R.string.error_red_sin_permiso,
            severity = Severity.Blocking,
            actions = listOf(UiErrorAction.Dismiss),
            errorCode = "NET-403",
        )
        is DomainError.Network.NotFound -> UiError(
            messageRes = R.string.error_red_no_encontrado,
            severity = Severity.Info,
            actions = listOf(UiErrorAction.Dismiss),
            errorCode = "NET-404",
        )
        is DomainError.Network.Parsing -> UiError(
            messageRes = R.string.error_red_formato,
            severity = Severity.Blocking,
            actions = listOf(UiErrorAction.Retry),
            errorCode = "NET-002",
        )
        is DomainError.Database.ReadFailed -> UiError(
            messageRes = R.string.error_bd_lectura,
            severity = Severity.Blocking,
            actions = listOf(UiErrorAction.Retry),
            errorCode = "DB-001",
        )
        is DomainError.Database.WriteFailed -> UiError(
            messageRes = R.string.error_bd_escritura,
            severity = Severity.Blocking,
            actions = listOf(UiErrorAction.Retry),
            errorCode = "DB-002",
        )
        is DomainError.Database.NotFound -> UiError(
            messageRes = R.string.error_bd_no_encontrado,
            severity = Severity.Info,
            actions = listOf(UiErrorAction.Dismiss),
            errorCode = "DB-003",
        )
        is DomainError.Database.IntegrityViolation -> UiError(
            messageRes = R.string.error_bd_integridad,
            severity = Severity.Blocking,
            actions = listOf(UiErrorAction.Dismiss),
            errorCode = "DB-004",
        )
        is DomainError.Security.BiometricUnavailable -> UiError(
            messageRes = R.string.error_seg_biometria_no_disponible,
            severity = Severity.Warning,
            actions = listOf(UiErrorAction.OpenSettings),
            errorCode = "SEC-001",
        )
        is DomainError.Security.BiometricLockout -> UiError(
            messageRes = R.string.error_seg_biometria_bloqueada,
            severity = Severity.Blocking,
            actions = listOf(UiErrorAction.Dismiss),
            errorCode = "SEC-002",
        )
        is DomainError.Security.RootDetected -> UiError(
            messageRes = R.string.error_seg_root_detectado,
            severity = Severity.Fatal,
            actions = emptyList(),
            errorCode = "SEC-003",
        )
        is DomainError.Security.IntegrityFailed -> UiError(
            messageRes = R.string.error_seg_integridad,
            severity = Severity.Fatal,
            actions = emptyList(),
            errorCode = "SEC-004",
        )
        is DomainError.Security.SessionExpired -> UiError(
            messageRes = R.string.error_seg_sesion_expirada,
            severity = Severity.Fatal,
            actions = listOf(UiErrorAction.Login),
            errorCode = "SEC-005",
        )
        is DomainError.Validation -> UiError(
            messageRes = R.string.error_validacion_formulario,
            severity = Severity.Warning,
            actions = listOf(UiErrorAction.Dismiss),
            errorCode = "VAL-001",
        )
        is DomainError.Unknown -> UiError(
            messageRes = R.string.error_desconocido,
            severity = Severity.Blocking,
            actions = listOf(UiErrorAction.Retry, UiErrorAction.Dismiss),
            errorCode = "UNK-000",
        )
    }
}
