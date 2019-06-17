package com.sudox.messenger.android.core

import com.sudox.messenger.android.core.controller.AppNavbarController
import com.sudox.messenger.android.core.controller.AppNavigationController

interface AppActivity {
    fun getNavbarController(): AppNavbarController
    fun getNavigationController(): AppNavigationController
}