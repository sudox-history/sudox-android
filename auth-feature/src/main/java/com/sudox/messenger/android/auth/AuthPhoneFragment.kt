package com.sudox.messenger.android.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sudox.design.phoneEditText.PhoneEditText
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.managers.ApplicationBarManager

class AuthPhoneFragment : Fragment() {

    private var appActivity: CoreActivity? = null
    private var applicationBarManager: ApplicationBarManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        appActivity = activity as CoreActivity
        applicationBarManager = appActivity!!.getApplicationBarManager().apply {
            setTitle(R.string.sign_in)
            showBackButton()
        }

        return inflater.inflate(R.layout.fragment_auth_phone, container, false).apply {
            findViewById<PhoneEditText>(R.id.authPhoneEditText).setCountry("RU", "7", com.sudox.design.R.drawable.ic_flag_russia)
        }
    }
}