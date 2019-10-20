package com.sudox.design

import android.app.Application

class DesignTestApplication : Application() {

    override fun onCreate() {
        setTheme(R.style.Sudox)
        super.onCreate()
    }
}