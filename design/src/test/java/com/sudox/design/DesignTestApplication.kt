package com.sudox.design

import android.app.Application

class DesignTestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Sudox)
    }
}