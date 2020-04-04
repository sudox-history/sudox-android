package com.sudox.messenger.android.auth.code

import android.content.Context
import android.view.View
import androidx.core.text.HtmlCompat
import com.sudox.design.codeedittext.CodeEditText
import com.sudox.design.edittext.layout.EditTextLayout
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.auth.inject.AuthComponent
import com.sudox.messenger.android.auth.vos.AuthScreenVO
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.countries.helpers.formatPhoneNumber
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import javax.inject.Inject

class AuthCodeScreenVO(
        val phoneNumber: String
) : AuthScreenVO {

    @Inject
    @JvmField
    var phoneNumberUtil: PhoneNumberUtil? = null
    var codeEditTextLayout: EditTextLayout? = null
    var codeEditText: CodeEditText? = null

    override fun getTitle(context: Context): String {
        return context.getString(R.string.welcome_back)
    }

    override fun getDescription(context: Context): Triple<Int, Int, CharSequence> {
        if (phoneNumberUtil == null) {
            ((context as CoreActivity).getLoaderComponent() as AuthComponent).inject(this)
        }

        val formattedPhone = phoneNumberUtil!!.formatPhoneNumber(phoneNumber)
        val spannable = HtmlCompat.fromHtml(context.getString(R.string.check_your_sms_and_enter_the_code, formattedPhone), 0)

        return Triple(R.drawable.ic_message, R.color.authscreenlayout_icon_tint_color, spannable)
    }

    override fun getChildViews(context: Context): Array<View> {
        if (codeEditTextLayout == null) {
            codeEditTextLayout = EditTextLayout(context).apply {
                codeEditText = CodeEditText(context).apply {
                    digitsCount = 5 // TODO: Get from API
                }

                childView = codeEditText
            }
        }

        return arrayOf(codeEditTextLayout as View)
    }
}