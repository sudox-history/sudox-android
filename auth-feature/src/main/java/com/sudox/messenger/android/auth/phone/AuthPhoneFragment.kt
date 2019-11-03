package com.sudox.messenger.android.auth.phone

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.phoneEditText.PhoneEditText
import com.sudox.design.regionsFlags
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.api.supportedRegions
import kotlinx.android.synthetic.main.fragment_auth_phone.authPhoneEditTextLayout
import java.util.Locale

class AuthPhoneFragment : Fragment(), ApplicationBarListener {

    override fun onButtonClicked(tag: Int) {
        authPhoneEditTextLayout.setErrorText(R.string.sudox_not_working_in_this_country)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val activity = activity as CoreActivity

        activity.getApplicationBarManager().let {
            it.reset()
            it.setListener(this)
            it.toggleButtonBack(true)
            it.toggleButtonNext(true)
            it.setTitleText(R.string.sign_in)
        }

        activity.getScreenManager().let {
            it.reset()
            it.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            it.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }

        return inflater.inflate(R.layout.fragment_auth_phone, container, false).apply {
            initPhoneEditText(this)
        }
    }

    private fun initPhoneEditText(view: View) {
        val phoneEditText = view.findViewById<PhoneEditText>(R.id.authPhoneEditText)
        val regionCode = Locale.getDefault().country

        var supportedCountryCode = supportedRegions[regionCode]
        val supportedRegionCode: String

        if (supportedCountryCode == null) {
            supportedRegionCode = supportedRegions.keys.first()
            supportedCountryCode = supportedRegions[supportedRegionCode]!!
        } else {
            supportedRegionCode = regionCode
        }

        phoneEditText.regionFlagIdCallback = ::handleCountryChangingAttempt
        phoneEditText.setCountry(supportedRegionCode, supportedCountryCode, regionsFlags[supportedRegionCode]!!)
    }

    private fun handleCountryChangingAttempt(regionCode: String): Int {
        if (!supportedRegions.containsKey(regionCode)) {
            authPhoneEditTextLayout.setErrorText(R.string.sudox_not_working_in_this_country)
            return 0
        }

        return regionsFlags[regionCode]!!
    }
}