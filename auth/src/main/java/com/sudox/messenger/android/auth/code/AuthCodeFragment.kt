package com.sudox.messenger.android.auth.code

import android.animation.Animator
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import com.sudox.messenger.android.auth.FROM_AUTH_CODE_TO_REGISTER_ACTION_ID
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreFragment
import kotlinx.android.synthetic.main.fragment_auth_code.authCodeDescriptionTextView
import kotlinx.android.synthetic.main.fragment_auth_code.authCodeEditText

class AuthCodeFragment : CoreFragment() {

    init {
        appBarVO = AuthCodeAppBarVO()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_auth_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val string = getString(R.string.check_sms, "+7 (901) 347-00-12")
        val spannable = HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_COMPACT)

        authCodeEditText.codeFilledCallback = {
            newNavigationManager!!.doAction(findNavController(), FROM_AUTH_CODE_TO_REGISTER_ACTION_ID)
        }

        authCodeDescriptionTextView.text = spannable
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
            authCodeEditText?.showSoftKeyboard()
        }

        super.onAnimationEnd(animation)
    }
}