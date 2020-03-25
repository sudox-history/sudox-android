package com.sudox.messenger.android.auth.phone

import android.animation.Animator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.sudox.design.appbar.AppBarVO
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.countriesProvider.countries
import com.sudox.design.countriesProvider.entries.Country
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.auth.code.AuthCodeFragment
import com.sudox.messenger.android.auth.country.AuthCountryFragment
import com.sudox.messenger.android.auth.country.COUNTRY_EXTRA_NAME
import com.sudox.messenger.android.core.CoreFragment
import kotlinx.android.synthetic.main.fragment_auth_phone.authPhoneEditText
import kotlinx.android.synthetic.main.fragment_auth_phone.authPhoneEditTextLayout
import kotlinx.android.synthetic.main.fragment_auth_phone.view.authPhoneEditText
import java.util.Locale

class AuthPhoneFragment : CoreFragment() {

    init {
        appBarVO = AuthPhoneAppBarVO()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_auth_phone, container, false).apply {
            initPhoneEditText(this)
        }
    }

    override fun onAppBarClicked(tag: Int) {
        super.onAppBarClicked(tag)

        if (tag == AUTH_PHONE_NEXT_BUTTON_TAG) {
            navigationManager!!.showChildFragment(AuthCodeFragment())
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            screenManager!!.let {
                it.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                it.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
    }

    override fun onAnimationEnd(animation: Animator) {
        view?.post {
            authPhoneEditText?.showSoftKeyboard()
        }

        super.onAnimationEnd(animation)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data!!.getParcelableExtra<Country>(COUNTRY_EXTRA_NAME)!!.let {
            authPhoneEditText.setCountry(it.regionCode, it.countryCode, it.flagImageId)
        }
    }

    private fun initPhoneEditText(view: View) = view.let {
        val regionCode = Locale.getDefault().country
        val country = countries[regionCode] ?: countries.values.elementAt(0)

        it.authPhoneEditText.setCountry(country.regionCode, country.countryCode, country.flagImageId)
        it.authPhoneEditText.regionFlagIdCallback = ::handleCountryChangingAttempt
        it.authPhoneEditText.countryCodeSelector.setOnClickListener {
            navigationManager!!.showChildFragment(AuthCountryFragment().apply {
                setTargetFragment(this@AuthPhoneFragment, 0)
            })
        }
    }

    private fun handleCountryChangingAttempt(regionCode: String): Int {
        val country = countries[regionCode]

        if (country == null) {
            authPhoneEditTextLayout.setErrorText(R.string.sudox_not_working_in_this_country)
            return 0
        }

        return country.flagImageId
    }
}