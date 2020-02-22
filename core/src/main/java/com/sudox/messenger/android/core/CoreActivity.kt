package com.sudox.messenger.android.core

import com.sudox.messenger.android.core.inject.CoreComponent

interface CoreActivity {
    fun getCoreComponent(): CoreComponent
    fun getLoader(): CoreLoader
}