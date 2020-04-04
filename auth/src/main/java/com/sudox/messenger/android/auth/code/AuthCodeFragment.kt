package com.sudox.messenger.android.auth.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.messenger.android.core.CoreFragment

class AuthCodeFragment : CoreFragment() {

    init {
        appBarVO = AuthCodeAppBarVO()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return null
    }
}