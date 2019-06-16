package com.sudox.messenger.auth.ui.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.sudox.messenger.auth.R
import com.sudox.messenger.core.fragment.AppFragment
import com.sudox.messenger.core.controller.AppNavigationController
import com.sudox.messenger.core.controller.AppNavbarController
import com.sudox.messenger.core.AppActivity
import com.sudox.messenger.core.fragment.AppFragmentType

class AuthPhoneFragment : AppFragment() {

    private var authPhoneViewModel: AuthPhoneViewModel? = null
    private var navbarController: AppNavbarController? = null
    private var navigationController: AppNavigationController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        initDependencies()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_auth_phone, container, false)
    }

    private fun initViewModel() {
        authPhoneViewModel = ViewModelProviders
                .of(this)
                .get(AuthPhoneViewModel::class.java)
    }

    private fun initDependencies() {
        val activity = activity as AppActivity
        navbarController = activity.getNavbarController()
        navigationController = activity.getNavigationController()
    }

    override fun getFragmentType(): Int {
        return AppFragmentType.AUTH
    }
}