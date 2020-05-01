package ru.sudox.android.auth.ui.code

import android.content.Context
import android.view.View
import androidx.core.text.HtmlCompat
import ru.sudox.android.auth.ui.R
import ru.sudox.android.auth.ui.vos.AuthScreenVO
import ru.sudox.design.codeedittext.CodeEditText
import ru.sudox.design.edittext.layout.EditTextLayout

class AuthCodeScreenVO(
        private val phoneNumber: String,
        private val userExists: Boolean
) : AuthScreenVO {

    var codeEditTextLayout: EditTextLayout? = null
    var codeEditText: CodeEditText? = null

    override fun getTitle(context: Context): String {
        return context.getString(if (userExists) {
            R.string.welcome_back
        } else {
            R.string.welcome_to_sudox
        })
    }

    override fun getDescription(context: Context): Triple<Int, Int, CharSequence> {
        return Triple(R.drawable.ic_message, R.color.authscreenlayout_icon_tint_color, HtmlCompat.fromHtml(
                context.getString(R.string.check_your_sms_and_enter_the_code, phoneNumber), 0
        ))
    }

    override fun getChildViews(context: Context): Array<View> {
        codeEditTextLayout = EditTextLayout(context).apply {
            codeEditText = CodeEditText(context).apply { digitsCount = 5 }
            childView = codeEditText
        }

        return arrayOf(codeEditTextLayout as View)
    }
}