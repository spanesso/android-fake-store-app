package com.mango.fakestore.core.analytics.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.mango.fakestore.core.analytics.ErrorRateLimiter
import com.mango.fakestore.core.analytics.EventTracker
import com.mango.fakestore.core.analytics.RandomSessionIdProvider
import com.mango.fakestore.core.analytics.SessionIdProvider
import com.mango.fakestore.core.analytics.Telemetry
import com.mango.fakestore.core.analytics.impl.FirebaseEventTrackerImpl
import com.mango.fakestore.core.analytics.impl.FirebaseTelemetryImpl
import com.mango.fakestore.core.logging.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
        FirebaseAnalytics.getInstance(context)

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    @Provides
    @Singleton
    fun provideFirebasePerformance(): FirebasePerformance = FirebasePerformance.getInstance()

    @Provides
    @Singleton
    fun provideSessionIdProvider(): SessionIdProvider = RandomSessionIdProvider()

    @Provides
    @Singleton
    fun provideErrorRateLimiter(): ErrorRateLimiter = ErrorRateLimiter()

    @Provides
    @Singleton
    fun provideTelemetry(
        crashlytics: FirebaseCrashlytics,
        analytics: FirebaseAnalytics,
        performance: FirebasePerformance,
        logger: Logger,
        sessionIdProvider: SessionIdProvider,
        rateLimiter: ErrorRateLimiter,
    ): Telemetry = FirebaseTelemetryImpl(crashlytics, analytics, performance, logger, sessionIdProvider, rateLimiter)

    @Provides
    @Singleton
    fun provideEventTracker(telemetry: Telemetry): EventTracker =
        FirebaseEventTrackerImpl(telemetry)
}
