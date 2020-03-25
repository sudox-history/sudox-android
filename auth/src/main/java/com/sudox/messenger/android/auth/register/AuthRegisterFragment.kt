package com.sudox.messenger.android.auth.register

import android.animation.Animator
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.showSoftKeyboard
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.managers.APPBAR_NEXT_BUTTON_TAG
import kotlinx.android.synthetic.main.fragment_auth_register.authRegisterEditTextLayout
import kotlinx.android.synthetic.main.fragment_auth_register.authRegisterNicknameEditText

class AuthRegisterFragment : CoreFragment(), ApplicationBarListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_auth_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authRegisterNicknameEditText.setNicknameTag("4566")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
//            applicationBarManager!!.let {
//                it.setListener(this)
//                it.toggleButtonBack(true)
//                it.toggleButtonNext(true)
//                it.setTitleText(R.string.sign_in)
//            }

            screenManager!!.let {
                it.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                it.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
    }

    override fun onButtonClicked(tag: Int) {
        if (tag == APPBAR_NEXT_BUTTON_TAG) {
            navigationManager!!.showMainPart()
        } else {
            authRegisterEditTextLayout.setErrorText(R.string.sudox_not_working_in_this_country)
        }
    }

    override fun onAnimationEnd(animation: Animator) {
        view?.post {
            authRegisterNicknameEditText?.showSoftKeyboard()
        }

        super.onAnimationEnd(animation)
    }
}