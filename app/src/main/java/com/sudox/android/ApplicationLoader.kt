package com.sudox.android

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.os.Bundle
import com.sudox.android.common.METRICA_API_KEY
import com.sudox.android.common.di.AppComponent
import com.sudox.android.common.di.DaggerAppComponent
import com.sudox.android.common.di.module.AppModule
import com.sudox.android.common.di.module.DatabaseModule
import com.sudox.android.data.repositories.RepositoriesContainer
import com.sudox.protocol.ProtocolClient
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class ApplicationLoader : DaggerApplication(), Application.ActivityLifecycleCallbacks {

    lateinit var protocolClient: ProtocolClient
    lateinit var repositoriesContainer: RepositoriesContainer

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        prepareDependencyInjection()

        // Super! ...
        super.onCreate()

        // Prepare metrica & bind app listeners ...
        prepareAppMetrica()
        registerActivityLifecycleCallbacks(this)
    }

    /**
     * Подготавливает Dependency Injection в работе.
     * Создает главный компонент, получает необходимые зависимости для данного класса.
     */
    private fun prepareDependencyInjection() {
        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .databaseModule(DatabaseModule())
                .build()

        // Get needed dependencies
        protocolClient = component.protocolClient()
        repositoriesContainer = component.repositoriesContainer()
    }

    /**
     * Подготавливает Yandex AppMetrica к работе.
     */
    private fun prepareAppMetrica() {
        val sharedPreferences = getSharedPreferences("com.sudox.android", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("firstRun", true)

        // It's first start
        if (isFirstRun) {
            sharedPreferences
                    .edit()
                    .putBoolean("firstRun", false)
                    .apply()
        }

        val config = YandexMetricaConfig
                .newConfigBuilder(METRICA_API_KEY)
                .handleFirstActivationAsUpdate(!isFirstRun)
                .withLogs()
                .withCrashReporting(true)
                .build()

        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }

    /**
     * Вызывается, когда запускается Activity.
     * Восстанавливает соединение с сервером, если оно отсутствует.
     */
    override fun onActivityResumed(activity: Activity) {
        if (!protocolClient.isWorking()) {
            protocolClient.connect()
        }
    }

    /**
     * Метод, выполняющий чистку памяти, используемой приложением.
     * Вызывается, когда приложение находится почти в начале LRU-списка.
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        // Close connection ...
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            protocolClient.kill()
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = component
    override fun onActivityStarted(p0: Activity?) {}
    override fun onActivityPaused(p0: Activity?) {}
    override fun onActivityDestroyed(p0: Activity?) {}
    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {}
    override fun onActivityStopped(p0: Activity?) {}
    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {}
}