package com.sudox.messenger.android.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.messenger.android.core.CoreFragment

class AuthRegisterFragment : CoreFragment() {

    init {
        appBarVO = AuthRegisterAppBarVO()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return null
    }
}