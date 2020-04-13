package ru.sudox.android.auth.ui.phone

import android.content.Context
import android.view.View
import ru.sudox.android.auth.R
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.BACK_BUTTON
import ru.sudox.design.appbar.vos.NOT_USED_PARAMETER

const val AUTH_PHONE_NEXT_BUTTON_TAG = 2

class AuthPhoneAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<Triple<Int, Int, Int>>? {
        return BACK_BUTTON
    }

    override fun getButtonsAtRight(): Array<Triple<Int, Int, Int>>? {
        return arrayOf(Triple(AUTH_PHONE_NEXT_BUTTON_TAG, NOT_USED_PARAMETER, R.string.next))
    }

    override fun getViewAtLeft(context: Context): View? {
        return null
    }

    override fun getViewAtRight(context: Context): View? {
        return null
    }

    override fun getTitle(): Int {
        return R.string.sign_in
    }
}