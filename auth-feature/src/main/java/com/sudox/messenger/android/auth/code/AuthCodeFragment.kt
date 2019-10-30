package com.sudox.messenger.android.auth.code

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreActivity
import kotlinx.android.synthetic.main.fragment_auth_code.authCodeDescriptionTextView

class AuthCodeFragment : Fragment() {

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

        return inflater.inflate(R.layout.fragment_auth_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formatDescription()
    }

    private fun formatDescription() {
        val string = getString(R.string.check_sms, "+7 (901) 347-00-12")
        val spannable = HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_COMPACT)

        authCodeDescriptionTextView.text = spannable
    }
}