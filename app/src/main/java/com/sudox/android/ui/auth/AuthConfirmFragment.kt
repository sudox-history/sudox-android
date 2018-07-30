package com.sudox.android.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sudox.android.R

// Email bundle key
internal val EMAIL_BUNDLE_KEY: String = "email"

class AuthConfirmFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        = inflater.inflate(R.layout.fragment_auth_confirm, container, false)
}