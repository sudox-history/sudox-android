package com.sudox.messenger.android.auth.ui.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.design.widgets.navbar.NavigationBarListener
import com.sudox.design.widgets.navbar.button.NavigationBarButton
import com.sudox.messenger.android.core.AppActivity
import com.sudox.messenger.android.core.controller.AppNavbarController
import com.sudox.messenger.android.core.controller.AppNavigationController
import com.sudox.messenger.android.core.fragment.AppFragment
import com.sudox.messenger.android.core.fragment.AppFragmentType
import com.sudox.messenger.auth.R

class AuthCodeFragment : AppFragment(), NavigationBarListener {
    internal var navbarController: AppNavbarController? = null
    internal var navigationController: AppNavigationController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initDependencies()
        initNavbar()

        return inflater.inflate(R.layout.fragment_auth_code, container, false)
    }

    override fun onButtonClicked(button: NavigationBarButton) {
        if (button == navbarController!!.getButtonStart()) {
            navigationController!!.showPreviousFragment()
        }
    }

    private fun initDependencies() {
        val activity = activity as AppActivity
        navbarController = activity.getNavbarController()
        navigationController = activity.getNavigationController()
    }

    private fun initNavbar() {
        navbarController!!.reset()
        navbarController!!.setListener(this)
        navbarController!!.toggleButtonBack(true)
    }

    override fun getFragmentType(): Int {
        return AppFragmentType.AUTH
    }
}