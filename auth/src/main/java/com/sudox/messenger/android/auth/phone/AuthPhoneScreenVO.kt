package com.sudox.messenger.android.auth.phone

import android.content.Context
import android.view.View
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.auth.vos.AuthScreenVO
import com.sudox.messenger.android.countries.views.PhoneEditText

class AuthPhoneScreenVO : AuthScreenVO {

    var phoneEditText: PhoneEditText? = null

    override fun getTitle(context: Context): String {
        return context.getString(R.string.phone_verification)
    }

    override fun getDescription(context: Context): Triple<Int, Int, String> {
        return Triple(
                R.drawable.ic_smartphone,
                R.color.authscreenlayout_icon_tint_color,
                context.getString(R.string.enter_your_phone_and_we_will_send_a_confirmation_code_for_you)
        )
    }

    override fun getChildViews(context: Context): Array<View> {
        if (phoneEditText == null) {
            phoneEditText = PhoneEditText(context)
        }

        return arrayOf(phoneEditText as View)
    }
}