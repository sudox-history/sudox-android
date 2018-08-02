package com.sudox.android

import com.sudox.android.di.AppComponent
import com.sudox.android.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.HasServiceInjector
import timber.log.Timber

class ApplicationLoader : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: AppComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build()
        component.inject(this)
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