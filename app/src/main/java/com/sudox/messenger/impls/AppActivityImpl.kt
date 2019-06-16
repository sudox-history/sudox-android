package com.sudox.messenger.impls

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sudox.messenger.R
import com.sudox.messenger.auth.ui.phone.AuthPhoneFragment
import com.sudox.messenger.core.AppActivity
import com.sudox.messenger.core.controller.AppNavbarController
import com.sudox.messenger.core.controller.AppNavigationController
import kotlinx.android.synthetic.main.activity_app.*

class AppActivityImpl : AppCompatActivity(), AppActivity {

    private var navbarController: AppNavbarController? = null
    private var navigationController: AppNavigationController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        navbarController = AppNavbarControllerImpl(appNavigationBar)
        navigationController = AppNavigationControllerImpl(R.id.appFrameLayout, supportFragmentManager)

        if (!navigationController!!.restoreState(savedInstanceState)) {
            navigationController!!.showFragment(AuthPhoneFragment(), false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationController!!.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun getNavbarController(): AppNavbarController {
        return navbarController!!
    }

    override fun getNavigationController(): AppNavigationController {
        return navigationController!!
    }
}