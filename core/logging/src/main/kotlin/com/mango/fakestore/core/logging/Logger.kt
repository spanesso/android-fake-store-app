package com.mango.fakestore.core.logging

interface Logger {
    fun info(tag: String, mensaje: String)
    fun warn(tag: String, mensaje: String, causa: Throwable? = null)
    fun error(tag: String, mensaje: String, causa: Throwable? = null)
}
