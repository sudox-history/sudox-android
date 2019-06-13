package com.sudox.design.widgets.navbar

import com.sudox.design.widgets.navbar.button.NavigationBarButton

interface NavigationBarListener {
    fun onButtonClicked(button: NavigationBarButton)
}