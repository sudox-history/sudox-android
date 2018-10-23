package com.sudox.android

import android.content.Context
import android.os.StrictMode
import com.sudox.android.common.API_KEY
import com.sudox.android.common.di.AppComponent
import com.sudox.android.common.di.DaggerAppComponent
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

class ApplicationLoader : DaggerApplication() {

    // Бывают случаи, что компонент Dagger'а нужен в Custom View
    companion object {
        lateinit var component: AppComponent
    }

    // Для класса AndroidInjection
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = component

    override fun onCreate() {
        // Создадим компонент
        component = DaggerAppComponent
                .builder()
                .application(this)
                .build()

        // А теперь скажем Android'у: "А блять, уебался!"
        super.onCreate()

        // Strict-mode
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll()
                .penaltyLog()
                .build())

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build())

        // Запуск статистики и т.п.
        val sharedPreferences = getSharedPreferences("com.sudox.android", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("firstRun", true)

        // Это первый запуск?
        if (isFirstLaunch) sharedPreferences.edit().putBoolean("firstRun", false).apply()

        // Initializing the AppMetrica SDK.
        val config = YandexMetricaConfig.newConfigBuilder(API_KEY)
                .handleFirstActivationAsUpdate(!isFirstLaunch)
                .withLogs()
                .build()

        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)

        // Enable Timber
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}