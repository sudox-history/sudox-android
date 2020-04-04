package com.sudox.messenger.android.auth.phone

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sudox.messenger.android.auth.AuthFragment
import com.sudox.messenger.android.auth.code.AuthCodeFragment
import com.sudox.messenger.android.countries.COUNTRY_CHANGE_REQUEST_CODE
import com.sudox.messenger.android.countries.COUNTRY_EXTRA_NAME
import com.sudox.messenger.android.countries.CountrySelectFragment

class AuthPhoneFragment : AuthFragment<AuthPhoneScreenVO>() {

    init {
        appBarVO = AuthPhoneAppBarVO()
        screenVO = AuthPhoneScreenVO()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        screenVO!!.phoneEditText!!.countrySelector.setOnClickListener {
            navigationManager!!.showChildFragment(CountrySelectFragment().apply {
                setTargetFragment(this@AuthPhoneFragment, COUNTRY_CHANGE_REQUEST_CODE)
            })
        }
    }

    override fun onAppBarClicked(tag: Int) {
        super.onAppBarClicked(tag)

        if (tag == AUTH_PHONE_NEXT_BUTTON_TAG) {
            navigationManager!!.showChildFragment(AuthCodeFragment())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == COUNTRY_CHANGE_REQUEST_CODE) {
            screenVO!!.phoneEditText!!.vo = data!!.getParcelableExtra(COUNTRY_EXTRA_NAME)
        }
    }
}