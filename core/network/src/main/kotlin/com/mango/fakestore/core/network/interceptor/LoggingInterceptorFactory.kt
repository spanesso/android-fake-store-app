package com.mango.fakestore.core.network.interceptor

import okhttp3.logging.HttpLoggingInterceptor

object LoggingInterceptorFactory {
    fun create(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
}
