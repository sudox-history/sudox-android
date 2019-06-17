package com.sudox.messenger.android.core.controller

import com.sudox.design.widgets.navbar.NavigationBarListener
import com.sudox.design.widgets.navbar.button.NavigationBarButton

interface AppNavbarController {
    fun getButtonStart(): NavigationBarButton
    fun getButtonNext(): NavigationBarButton
    fun toggleButtonBack(toggle: Boolean)
    fun toggleButtonNext(toggle: Boolean)
    fun setListener(listener: NavigationBarListener?)
    fun reset()
}