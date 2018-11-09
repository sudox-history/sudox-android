package com.sudox.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import com.crashlytics.android.Crashlytics
import com.sudox.android.common.API_KEY
import com.sudox.android.common.di.AppComponent
import com.sudox.android.common.di.DaggerAppComponent
import com.sudox.protocol.ProtocolClient
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

class ApplicationLoader : DaggerApplication(), Application.ActivityLifecycleCallbacks {

    // Бывают случаи, что компонент Dagger'а нужен в Custom View
    companion object {
        lateinit var component: AppComponent
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = component

    @Inject
    lateinit var protocolClient: ProtocolClient

    override fun onCreate() {
        component = DaggerAppComponent
                .builder()
                .application(this)
                .build()

        component.inject(this)

        super.onCreate()
        registerActivityLifecycleCallbacks(this)

        Fabric.with(this, Crashlytics())

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

    override fun onActivityPaused(p0: Activity?) {}

    override fun onActivityResumed(p0: Activity?) {

        if (!protocolClient.isValid()) {
            protocolClient.connect()
        }
    }

    override fun onActivityStarted(p0: Activity?) {}

    override fun onActivityDestroyed(p0: Activity?) {}

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {}

    override fun onActivityStopped(p0: Activity?) {}

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {}


}