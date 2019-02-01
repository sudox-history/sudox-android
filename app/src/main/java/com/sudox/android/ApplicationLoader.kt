package com.sudox.android

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.sudox.android.common.API_KEY
import com.sudox.android.common.di.AppComponent
import com.sudox.android.common.di.DaggerAppComponent
import com.sudox.android.data.repositories.RepositoriesContainer
import com.sudox.protocol.ProtocolClient
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.fabric.sdk.android.Fabric
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class ApplicationLoader : DaggerApplication(), Application.ActivityLifecycleCallbacks {

    // Бывают случаи, что компонент Dagger'а нужен в Custom View
    companion object {
        lateinit var component: AppComponent

        /**
         * НЕ ЗАБЫВАТЬ ОБНОВЛЯТЬ ЭТУ КОНСТАНТУ!
         */
        var version: String = "0.5.0"
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = component

    @Inject
    lateinit var protocolClient: ProtocolClient

    @Inject
    lateinit var repositoriesContainer: RepositoriesContainer

    // Репозитории начнут слушать события после создания этого обьекта.

    override fun onCreate() {
        component = DaggerAppComponent
                .builder()
                .application(this)
                .build()

        component.inject(this)

        super.onCreate()
        registerActivityLifecycleCallbacks(this)

        Fabric.with(this, Crashlytics())

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
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            protocolClient.kill()
        }
    }

    override fun onActivityPaused(p0: Activity?) {}

    override fun onActivityResumed(activity: Activity) {
        GlobalScope.launch(Dispatchers.IO) {
            if (!protocolClient.isWorking())
                protocolClient.connect()
        }
    }

    override fun onActivityStarted(p0: Activity?) {}
    override fun onActivityDestroyed(p0: Activity?) {}
    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {}
    override fun onActivityStopped(p0: Activity?) {}
    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {}
}