package com.sudox.messenger.android.auth.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreActivity
import kotlinx.android.synthetic.main.fragment_auth_code.authCodeDescriptionTextView

class AuthCodeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (activity as CoreActivity).getApplicationBarManager().let {
            it.reset()
            it.showBackButton()
            it.setTitle(R.string.sign_in)
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