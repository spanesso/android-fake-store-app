package com.mango.fakestore.core.testing.builders

import com.mango.fakestore.core.error.UiError

fun uiErrorInfo(
    messageRes: Int = 0,
    errorCode: String = "INFO-001",
    actions: List<UiError.UiErrorAction> = listOf(UiError.UiErrorAction.Dismiss),
): UiError = UiError(
    messageRes = messageRes,
    severity = UiError.Severity.Info,
    actions = actions,
    errorCode = errorCode,
)

fun uiErrorWarning(
    messageRes: Int = 0,
    errorCode: String = "WARN-001",
    actions: List<UiError.UiErrorAction> = listOf(UiError.UiErrorAction.Retry),
): UiError = UiError(
    messageRes = messageRes,
    severity = UiError.Severity.Warning,
    actions = actions,
    errorCode = errorCode,
)

fun uiErrorBlocking(
    messageRes: Int = 0,
    errorCode: String = "BLOCK-001",
    actions: List<UiError.UiErrorAction> = emptyList(),
): UiError = UiError(
    messageRes = messageRes,
    severity = UiError.Severity.Blocking,
    actions = actions,
    errorCode = errorCode,
)

fun uiErrorFatal(
    messageRes: Int = 0,
    errorCode: String = "FATAL-001",
    actions: List<UiError.UiErrorAction> = emptyList(),
): UiError = UiError(
    messageRes = messageRes,
    severity = UiError.Severity.Fatal,
    actions = actions,
    errorCode = errorCode,
)
