package com.sudox.messenger.android

import android.app.Application
import com.sudox.design.R

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Sudox)
    }
}