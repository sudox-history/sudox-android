package com.sudox.messenger.android.auth.code

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.auth.register.AuthRegisterFragment
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.managers.NavigationManager
import kotlinx.android.synthetic.main.fragment_auth_code.*

class AuthCodeFragment : Fragment(), ApplicationBarListener {

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
            it.setInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }

        navigationManager = activity.getNavigationManager()

        return inflater.inflate(R.layout.fragment_auth_code, container, false)
    }

    override fun onResume() {
        super.onResume()
        authCodeEditText.showOnFocusedKeyboard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val string = getString(R.string.check_sms, "+7 (901) 347-00-12")
        val spannable = HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_COMPACT)

        authCodeDescriptionTextView.text = spannable
    }

    override fun onButtonClicked(tag: Int) {
        navigationManager!!.showFragment(AuthRegisterFragment(), true)
    }
}