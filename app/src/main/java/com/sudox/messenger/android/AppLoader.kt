package com.sudox.messenger.android

import android.app.Application
import com.sudox.design.initDesign

class AppLoader : Application() {

    override fun onCreate() {
        super.onCreate()
        initDesign(this)
    }
}