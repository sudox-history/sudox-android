package com.sudox.messenger.android.auth.register

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.showSoftKeyboard
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreActivity
import kotlinx.android.synthetic.main.fragment_auth_register.authRegisterEditTextLayout
import kotlinx.android.synthetic.main.fragment_auth_register.authRegisterNicknameEditText

class AuthRegisterFragment : Fragment(), ApplicationBarListener {

    override fun onButtonClicked(tag: Int) {
        authRegisterEditTextLayout.setErrorText(R.string.sudox_not_working_in_this_country)
    }

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
            it.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        return inflater.inflate(R.layout.fragment_auth_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRegisterNicknameEditText.setNicknameTag("4566")
        authRegisterNicknameEditText.showSoftKeyboard()
    }
}