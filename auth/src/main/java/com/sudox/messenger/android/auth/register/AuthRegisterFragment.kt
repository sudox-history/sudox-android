package com.sudox.messenger.android.auth.register

import android.animation.Animator
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.sudox.design.showSoftKeyboard
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreFragment
import kotlinx.android.synthetic.main.fragment_auth_register.authRegisterNicknameEditText

class AuthRegisterFragment : CoreFragment() {

    init {
        appBarVO = AuthRegisterAppBarVO()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_auth_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authRegisterNicknameEditText.setNicknameTag("4566")
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

    override fun onAppBarClicked(tag: Int) {
        super.onAppBarClicked(tag)

        if (tag == AUTH_REGISTER_FINISH_BUTTON_TAG) {
            navigationManager!!.showMainPart()
        }
    }

    override fun onAnimationEnd(animation: Animator) {
        view?.post {
            authRegisterNicknameEditText?.showSoftKeyboard()
        }

        super.onAnimationEnd(animation)
    }
}