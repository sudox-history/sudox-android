package com.sudox.messenger.android.auth.ui.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.messenger.auth.R
import com.sudox.messenger.android.core.fragment.AppFragment
import com.sudox.messenger.android.core.fragment.AppFragmentType

class AuthCodeFragment : AppFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_auth_code, container, false)
    }

    override fun getFragmentType(): Int {
        return AppFragmentType.AUTH
    }
}