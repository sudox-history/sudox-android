package com.sudox.messenger.android.auth.phone

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.common.findCountryByRegionCode
import com.sudox.design.common.supportedCountries
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.auth.code.AuthCodeFragment
import com.sudox.messenger.android.auth.country.AuthCountryFragment
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.managers.NavigationManager
import kotlinx.android.synthetic.main.fragment_auth_phone.authPhoneEditTextLayout
import kotlinx.android.synthetic.main.fragment_auth_phone.view.authPhoneEditText
import java.util.Locale

class AuthPhoneFragment : Fragment(), ApplicationBarListener {

    private var navigationManager: NavigationManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val activity = activity as CoreActivity

        activity.getApplicationBarManager().let {
            it.setListener(this)
            it.toggleButtonBack(true)
            it.toggleButtonNext(true)
            it.setTitleText(R.string.sign_in)
        }

        activity.getScreenManager().let {
            it.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            it.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }

        navigationManager = activity.getNavigationManager()

        return inflater.inflate(R.layout.fragment_auth_phone, container, false).apply {
            initPhoneEditText(this)
        }
    }

    override fun onButtonClicked(tag: Int) {
        navigationManager!!.showFragment(AuthCodeFragment(), true)
    }

    private fun initPhoneEditText(view: View) = view.let {
        val regionCode = Locale.getDefault().country
        val country = findCountryByRegionCode(regionCode) ?: supportedCountries[0]

        it.authPhoneEditText.setCountry(country.regionCode, country.countryCode, country.flagImageId)
        it.authPhoneEditText.regionFlagIdCallback = ::handleCountryChangingAttempt
        it.authPhoneEditText.countryCodeSelector.setOnClickListener {
            navigationManager!!.showFragment(AuthCountryFragment(), true)
        }
    }

    private fun handleCountryChangingAttempt(regionCode: String): Int {
        val country = findCountryByRegionCode(regionCode)

        if (country == null) {
            authPhoneEditTextLayout.setErrorText(R.string.sudox_not_working_in_this_country)
            return 0
        }

        return country.flagImageId
    }
}