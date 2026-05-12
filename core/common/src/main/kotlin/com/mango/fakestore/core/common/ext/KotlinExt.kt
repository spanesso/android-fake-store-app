package com.mango.fakestore.core.common.ext

fun String?.isNotNullOrBlank(): Boolean = !isNullOrBlank()

fun String.truncate(maxLength: Int, ellipsis: String = "..."): String =
    if (length <= maxLength) this else take(maxLength - ellipsis.length) + ellipsis

inline fun <T : Any, R> T?.ifNotNull(action: (T) -> R): R? = this?.let(action)

fun <T> T?.orDefault(default: T): T = this ?: default

fun <T> Collection<T>.toImmutableList(): List<T> = toList()
