package com.sudox.messenger.android.auth.ui.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.design.widgets.navbar.NAVBAR_START_BUTTON_TAG
import com.sudox.messenger.android.core.AppActivity
import com.sudox.messenger.android.core.controller.AppNavbarController
import com.sudox.messenger.android.core.controller.AppNavigationController
import com.sudox.messenger.android.core.fragment.AppFragment
import com.sudox.messenger.android.core.fragment.AppFragmentType
import com.sudox.messenger.android.auth.R

class AuthCodeFragment : AppFragment() {

    internal var navbarController: AppNavbarController? = null
    internal var navigationController: AppNavigationController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initDependencies()
        initNavbar()

        return inflater.inflate(R.layout.fragment_auth_code, container, false)
    }

    private fun initDependencies() {
        val activity = activity as AppActivity
        navbarController = activity.getNavbarController()
        navigationController = activity.getNavigationController()
    }

    private fun initNavbar() {
        navbarController!!.reset()
        navbarController!!.setButtonsClickCallback(::handleNavbarButtonClick)
        navbarController!!.toggleButtonBack(true)
    }

    private fun handleNavbarButtonClick(tag: Int) {
        if (tag == NAVBAR_START_BUTTON_TAG) {
            navigationController!!.showPreviousFragment()
        }
    }

    override fun getFragmentType(): Int {
        return AppFragmentType.AUTH
    }
}