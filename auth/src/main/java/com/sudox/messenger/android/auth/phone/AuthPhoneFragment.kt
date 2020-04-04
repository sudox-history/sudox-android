package com.sudox.messenger.android.auth.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.messenger.android.auth.views.AuthScreenLayout
import com.sudox.messenger.android.core.CoreFragment

class AuthPhoneFragment : CoreFragment() {

    private var screenVO = AuthPhoneScreenVO()

    init {
        appBarVO = AuthPhoneAppBarVO()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return AuthScreenLayout(context!!).apply {
            vo = screenVO
        }
    }
}