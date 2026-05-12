package com.mango.fakestore.core.ui.ext

import android.content.Context

fun Context.dpToPx(dp: Float): Float = dp * resources.displayMetrics.density

fun Context.pxToDp(px: Float): Float = px / resources.displayMetrics.density
