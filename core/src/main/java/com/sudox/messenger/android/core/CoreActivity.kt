package com.sudox.messenger.android.core

import com.sudox.messenger.android.core.managers.ApplicationBarManager
import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.core.managers.ScreenManager

interface CoreActivity {
    fun getScreenManager(): ScreenManager
    fun getNavigationManager(): NavigationManager
    fun getApplicationBarManager(): ApplicationBarManager
}