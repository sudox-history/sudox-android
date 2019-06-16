package com.sudox.messenger.auth.ui.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sudox.api.auth.AuthErrorCodes
import com.sudox.design.widgets.navbar.NavigationBarListener
import com.sudox.design.widgets.navbar.button.NavigationBarButton
import com.sudox.messenger.auth.R
import com.sudox.messenger.auth.data.AuthSession
import com.sudox.messenger.auth.ui.AuthSharedViewModel
import com.sudox.messenger.auth.ui.code.AuthCodeFragment
import com.sudox.messenger.core.AppActivity
import com.sudox.messenger.core.controller.AppNavbarController
import com.sudox.messenger.core.controller.AppNavigationController
import com.sudox.messenger.core.fragment.AppFragment
import com.sudox.messenger.core.fragment.AppFragmentType
import kotlinx.android.synthetic.main.fragment_auth_phone.*

class AuthPhoneFragment : AppFragment(), NavigationBarListener {

    internal var authSharedViewModel: AuthSharedViewModel? = null
    internal var authPhoneViewModel: AuthPhoneViewModel? = null
    internal var navbarController: AppNavbarController? = null
    internal var navigationController: AppNavigationController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initDependencies()
        initNavbar()

        return inflater.inflate(R.layout.fragment_auth_phone, container, false)
    }

    override fun onButtonClicked(button: NavigationBarButton) {
        if (button == navbarController!!.getButtonNext()) {
            val phone = authPhoneEditText.text.toString()
            val sessionLiveData = authSharedViewModel!!.sessionLiveData
            authPhoneViewModel!!.requestCode(phone, sessionLiveData)
        }
    }

    private fun initViewModel() {
        authSharedViewModel = ViewModelProviders
                .of(activity!!)
                .get(AuthSharedViewModel::class.java)

        authPhoneViewModel = ViewModelProviders
                .of(this)
                .get(AuthPhoneViewModel::class.java)

        authPhoneViewModel!!.errorsLiveData.observe(this, handleError())
        authSharedViewModel!!.sessionLiveData.observe(this, handleSession())
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

    private fun handleError() = Observer<Int> {
        authPhoneEditTextLayout.setErrorTextRes(if (it == AuthErrorCodes.INVALID_PARAMETERS) {
            R.string.wrong_phone_format
        } else if (it == AuthErrorCodes.TOO_MANY_REQUESTS) {
            R.string.too_many_requests
        } else {
            R.string.unknown_error
        })
    }

    private fun handleSession() = Observer<AuthSession> {
        navigationController!!.showFragment(AuthCodeFragment())
    }

    override fun getFragmentType(): Int {
        return AppFragmentType.AUTH
    }
}