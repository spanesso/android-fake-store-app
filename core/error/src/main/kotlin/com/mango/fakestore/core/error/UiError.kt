package com.mango.fakestore.core.error

import androidx.annotation.StringRes

data class UiError(
    @StringRes val messageRes: Int,
    val severity: Severity,
    val actions: List<UiErrorAction>,
    val errorCode: String,
) {
    enum class Severity { Info, Warning, Blocking, Fatal }

    sealed interface UiErrorAction {
        data object Retry : UiErrorAction
        data object Dismiss : UiErrorAction
        data object Login : UiErrorAction
        data object OpenSettings : UiErrorAction
    }
}
