package com.sudox.messenger.android.auth.phone

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.sudox.design.phoneEditText.PhoneEditText
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreActivity

class AuthPhoneFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val activity = activity as CoreActivity

        activity.getApplicationBarManager().let {
            it.reset()
            it.showBackButton()
            it.setTitle(R.string.sign_in)
        }

        activity.getScreenManager().let {
            it.reset()
            it.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            it.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }

        return inflater.inflate(R.layout.fragment_auth_phone, container, false).apply {
            findViewById<PhoneEditText>(R.id.authPhoneEditText).apply {
                setCountry("RU", 7, com.sudox.design.R.drawable.ic_flag_russia)
                regionFlagIdCallback = { R.drawable.ic_flag_russia }
            }
        }
    }
}