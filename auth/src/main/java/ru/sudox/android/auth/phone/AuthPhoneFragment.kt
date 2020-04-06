package ru.sudox.android.auth.phone

import android.content.Intent
import android.os.Bundle
import android.view.View
import ru.sudox.android.auth.AuthFragment
import ru.sudox.android.auth.code.AuthCodeFragment
import ru.sudox.android.countries.COUNTRY_CHANGE_REQUEST_CODE
import ru.sudox.android.countries.COUNTRY_EXTRA_NAME
import ru.sudox.android.countries.CountrySelectFragment

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