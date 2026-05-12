package com.mango.fakestore.core.logging.impl

import com.mango.fakestore.core.logging.Logger

class NoOpLogger : Logger {
    override fun info(tag: String, mensaje: String) = Unit
    override fun warn(tag: String, mensaje: String, causa: Throwable?) = Unit
    override fun error(tag: String, mensaje: String, causa: Throwable?) = Unit
}
