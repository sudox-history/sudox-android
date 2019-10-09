package com.sudox.messenger.android.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sudox.design.initDesign
import com.sudox.design.phoneEditText.PhoneEditText

class AuthPhoneFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initDesign(activity!!)
        val view = inflater.inflate(R.layout.fragment_auth_phone, container, false)
        view.findViewById<PhoneEditText>(R.id.phoneEditText)
                .setCountry("RU", "7", com.sudox.design.R.drawable.ic_flag_russia)

        return view
    }
}