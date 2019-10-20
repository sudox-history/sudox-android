package com.sudox.messenger.android.core

import com.sudox.messenger.android.core.managers.ApplicationBarManager
import com.sudox.messenger.android.core.managers.NavigationManager

interface CoreActivity {
    fun getNavigationManager(): NavigationManager
    fun getApplicationBarManager(): ApplicationBarManager
}