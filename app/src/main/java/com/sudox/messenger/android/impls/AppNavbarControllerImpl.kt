package com.sudox.messenger.android.impls

import android.view.View
import com.sudox.design.widgets.navbar.NavigationBar
import com.sudox.design.widgets.navbar.NavigationBarListener
import com.sudox.design.widgets.navbar.button.NavigationBarButton
import com.sudox.design.widgets.navbar.button.NavigationBarButtonIconDirection
import com.sudox.messenger.R
import com.sudox.messenger.android.core.controller.AppNavbarController

class AppNavbarControllerImpl(val navigationBar: NavigationBar) : AppNavbarController {
    override fun getButtonStart(): NavigationBarButton {
        return navigationBar.buttonStart!!
    }

    override fun getButtonNext(): NavigationBarButton {
        return navigationBar.buttonsEnd[0]!!
    }

    override fun toggleButtonBack(toggle: Boolean) {
        val button = navigationBar.buttonStart!!

        if (toggle) {
            button.setIconDrawableRes(R.drawable.ic_arrow_nav_start)
            button.setIconDirection(NavigationBarButtonIconDirection.START)
            button.setTextRes(R.string.back)
            button.isClickable = true
            button.visibility = View.VISIBLE
        } else {
            button.resetView()
        }
    }

    override fun toggleButtonNext(toggle: Boolean) {
        val button = navigationBar.buttonsEnd[0]!!

        if (toggle) {
            button.setIconDrawableRes(R.drawable.ic_arrow_nav_end)
            button.setIconDirection(NavigationBarButtonIconDirection.END)
            button.setTextRes(R.string.next)
            button.isClickable = true
            button.visibility = View.VISIBLE
        } else {
            button.resetView()
        }
    }

    override fun setListener(listener: NavigationBarListener?) {
        navigationBar.listener = listener
    }

    override fun reset() {
        navigationBar.resetView()
    }
}