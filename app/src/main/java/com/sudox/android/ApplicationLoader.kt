package com.sudox.android

import com.sudox.android.di.AppComponent
import com.sudox.android.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

class ApplicationLoader : DaggerApplication() {

    lateinit var component: AppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        component = DaggerAppComponent
                .builder()
                .application(this)
                .build()

        return component
    }

    override fun onCreate() {
        super.onCreate()

        // Enable Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}