package com.mango.fakestore.core.security.integrity

import android.content.Context
import com.mango.fakestore.core.logging.Logger
import com.scottyab.rootbeer.RootBeer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IntegrityCheckerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger,
) : IntegrityChecker {

    override fun estaComprometido(): Boolean {
        val comprometido = RootBeer(context).isRooted
        if (comprometido) {
            logger.warn(TAG, "Dispositivo comprometido detectado (root/jailbreak)")
        } else {
            logger.info(TAG, "Integridad del dispositivo verificada — sin root detectado")
        }
        return comprometido
    }

    private companion object {
        const val TAG = "IntegrityChecker"
    }
}
