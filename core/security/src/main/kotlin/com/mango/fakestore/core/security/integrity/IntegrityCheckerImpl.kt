package com.mango.fakestore.core.security.integrity

import android.content.Context
import com.scottyab.rootbeer.RootBeer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IntegrityCheckerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : IntegrityChecker {
    override fun estaComprometido(): Boolean = RootBeer(context).isRooted
}
