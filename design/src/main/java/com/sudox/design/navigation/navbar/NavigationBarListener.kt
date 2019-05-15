package com.sudox.design.navigation.navbar

interface NavigationBarListener {
    fun onButtonClick(@NavigationBarButton.Type buttonType: Int)
}