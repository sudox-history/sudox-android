package com.sudox.messenger.android.auth.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sudox.design.phoneEditText.PhoneEditText
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreActivity

class AuthPhoneFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (activity as CoreActivity).getApplicationBarManager().let {
            it.reset()
            it.showBackButton()
            it.setTitle(R.string.sign_in)
        }

        return inflater.inflate(R.layout.fragment_auth_phone, container, false).apply {
            findViewById<PhoneEditText>(R.id.authPhoneEditText).setCountry("RU", "7", com.sudox.design.R.drawable.ic_flag_russia)
        }
    }
}