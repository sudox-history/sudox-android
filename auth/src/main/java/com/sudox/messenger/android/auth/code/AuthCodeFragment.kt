package com.sudox.messenger.android.auth.code

import android.animation.Animator
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.text.HtmlCompat
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.auth.register.AuthRegisterFragment
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.managers.NavigationManager
import kotlinx.android.synthetic.main.fragment_auth_code.authCodeDescriptionTextView
import kotlinx.android.synthetic.main.fragment_auth_code.authCodeEditText

class AuthCodeFragment : CoreFragment(), ApplicationBarListener {

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
            it.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        navigationManager = activity.getNavigationManager()

        return inflater.inflate(R.layout.fragment_auth_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val string = getString(R.string.check_sms, "+7 (901) 347-00-12")
        val spannable = HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_COMPACT)

        authCodeDescriptionTextView.text = spannable
    }

    override fun onAnimationEnd(animation: Animator) {
        view?.post {
            authCodeEditText.showSoftKeyboard()
        }

        super.onAnimationEnd(animation)
    }

    override fun onButtonClicked(tag: Int) {
        navigationManager!!.showFragment(AuthRegisterFragment(), true)
    }
}