package com.mango.fakestore.core.logging.impl

import com.mango.fakestore.core.logging.Logger
import timber.log.Timber

class TimberLogger : Logger {

    init {
        if (Timber.treeCount == 0) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun info(tag: String, mensaje: String) {
        Timber.tag(tag).d(mensaje)
    }

    override fun warn(tag: String, mensaje: String, causa: Throwable?) {
        if (causa != null) Timber.tag(tag).w(causa, mensaje)
        else Timber.tag(tag).w(mensaje)
    }

    override fun error(tag: String, mensaje: String, causa: Throwable?) {
        if (causa != null) Timber.tag(tag).e(causa, mensaje)
        else Timber.tag(tag).e(mensaje)
    }
}
