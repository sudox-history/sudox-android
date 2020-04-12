package ru.sudox.android.auth.code

import android.content.Context
import android.view.View
import androidx.core.text.HtmlCompat
import ru.sudox.design.codeedittext.CodeEditText
import ru.sudox.design.edittext.layout.EditTextLayout
import ru.sudox.android.auth.R
import ru.sudox.android.auth.inject.AuthComponent
import ru.sudox.android.auth.vos.AuthScreenVO
import ru.sudox.android.core.CoreActivity
import ru.sudox.android.countries.helpers.formatPhoneNumber
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
        codeEditTextLayout = EditTextLayout(context).apply {
            codeEditText = CodeEditText(context).apply {
                digitsCount = 5 // TODO: Get from API
            }

            childView = codeEditText
        }

        return arrayOf(codeEditTextLayout as View)
    }
}