package ru.sudox.android.auth.register

import android.content.Context
import android.view.View
import ru.sudox.design.edittext.BasicEditText
import ru.sudox.design.edittext.layout.EditTextLayout
import ru.sudox.android.auth.R
import ru.sudox.android.auth.vos.AuthScreenVO

class AuthRegisterScreenVO : AuthScreenVO {

    var nicknameEditText: BasicEditText? = null
    var nicknameEditTextLayout: EditTextLayout? = null
    var nameEditTextLayout: EditTextLayout? = null
    var nameEditText: BasicEditText? = null

    override fun getTitle(context: Context): String {
        return context.getString(R.string.just_a_last_step)
    }

    override fun getDescription(context: Context): Triple<Int, Int, CharSequence> {
        return Triple(
                R.drawable.ic_account,
                R.color.authscreenlayout_icon_tint_color,
                context.getString(R.string.imagine_what_nickname_you_want_your_friends_will_see)
        )
    }

    override fun getChildViews(context: Context): Array<View> {
        nicknameEditTextLayout = EditTextLayout(context).apply {
            nicknameEditText = BasicEditText(context).apply {
                hint = context.getString(R.string.nickname)
                isSingleLine = true
                maxLines = 1
            }

            childView = nicknameEditText
        }

        nameEditTextLayout = EditTextLayout(context).apply {
            nameEditText = BasicEditText(context).apply {
                hint = context.getString(R.string.name_and_surname)
                isSingleLine = true
                maxLines = 1
            }

            childView = nameEditText
        }

        return arrayOf(nicknameEditTextLayout as View, nameEditTextLayout as View)
    }
}