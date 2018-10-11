package com.sudox.android

import android.content.Context
import android.content.SharedPreferences
import com.sudox.android.common.API_KEY
import com.sudox.android.common.di.DaggerAppComponent
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

class ApplicationLoader : DaggerApplication() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent
                .builder()
                .application(this)
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("com.sudox.android", Context.MODE_PRIVATE)

        val isFirstLaunch = sharedPreferences.getBoolean("firstRun", true)

        if (isFirstLaunch) {
            sharedPreferences.edit().putBoolean("firstRun", false).apply()
        }

        // Initializing the AppMetrica SDK.
        val config = YandexMetricaConfig.newConfigBuilder(API_KEY)
                .handleFirstActivationAsUpdate(!isFirstLaunch)
                .withLogs()
                .build()
        YandexMetrica.activate(applicationContext, config)
        // Tracking user activity.
        YandexMetrica.enableActivityAutoTracking(this)

        // Enable Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}