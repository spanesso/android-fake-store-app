package com.mango.fakestore.core.network.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mango.fakestore.core.common.dispatchers.AppDispatchers
import com.mango.fakestore.core.network.BuildConfig
import com.mango.fakestore.core.network.config.NetworkConfig
import com.mango.fakestore.core.network.connectivity.ConnectivityObserver
import com.mango.fakestore.core.network.connectivity.ConnectivityObserverImpl
import com.mango.fakestore.core.network.interceptor.LoggingInterceptorFactory
import com.mango.fakestore.core.network.interceptor.RetryInterceptor
import com.mango.fakestore.core.network.reporter.NetworkErrorReporter
import com.mango.fakestore.core.network.reporter.NoOpNetworkErrorReporter
import com.mango.fakestore.core.network.ssl.CertificatePinnerFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkConfig(): NetworkConfig = NetworkConfig(baseUrl = BuildConfig.BASE_URL)

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideOkHttpClient(config: NetworkConfig): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)
            .certificatePinner(CertificatePinnerFactory.create(config))
            .addInterceptor(RetryInterceptor(config.maxRetries, config.retryBaseDelayMs, config.retryMaxDelayMs))

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(LoggingInterceptorFactory.create())
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json, config: NetworkConfig): Retrofit =
        Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindsModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(impl: ConnectivityObserverImpl): ConnectivityObserver

    @Binds
    @Singleton
    abstract fun bindNetworkErrorReporter(impl: NoOpNetworkErrorReporter): NetworkErrorReporter
}
