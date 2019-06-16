package com.sudox.messenger.core

import com.sudox.messenger.core.controller.AppNavbarController
import com.sudox.messenger.core.controller.AppNavigationController

interface AppActivity {
    fun getNavbarController(): AppNavbarController
    fun getNavigationController(): AppNavigationController
}