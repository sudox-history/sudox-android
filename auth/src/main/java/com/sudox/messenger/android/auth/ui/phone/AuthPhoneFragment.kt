package com.sudox.messenger.android.auth.ui.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.design.widgets.navbar.NavigationBarListener
import com.sudox.design.widgets.navbar.button.NavigationBarButton
import com.sudox.messenger.android.auth.ui.code.AuthCodeFragment
import com.sudox.messenger.android.core.AppActivity
import com.sudox.messenger.android.core.controller.AppNavbarController
import com.sudox.messenger.android.core.controller.AppNavigationController
import com.sudox.messenger.android.core.fragment.AppFragment
import com.sudox.messenger.android.core.fragment.AppFragmentType
import com.sudox.messenger.auth.R

class AuthPhoneFragment : AppFragment(), NavigationBarListener {
    internal var navbarController: AppNavbarController? = null
    internal var navigationController: AppNavigationController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initDependencies()
        initNavbar()

        return inflater.inflate(R.layout.fragment_auth_phone, container, false)
    }

    override fun onButtonClicked(button: NavigationBarButton) {
        if (button == navbarController!!.getButtonNext()) {
            navigationController!!.showFragment(AuthCodeFragment())
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
        navbarController!!.toggleButtonNext(true)
    }

    override fun getFragmentType(): Int {
        return AppFragmentType.AUTH
    }
}