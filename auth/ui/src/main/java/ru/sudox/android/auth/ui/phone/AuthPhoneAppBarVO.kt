package ru.sudox.android.auth.ui.phone

import android.content.Context
import android.view.View
import ru.sudox.android.auth.ui.R
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.APPBAR_BACK_BUTTON_PARAMS
import ru.sudox.design.appbar.vos.others.AppBarButtonParam
import ru.sudox.design.appbar.vos.others.NOT_USED_PARAMETER

const val AUTH_PHONE_NEXT_BUTTON_TAG = 2

class AuthPhoneAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<AppBarButtonParam>? {
        return APPBAR_BACK_BUTTON_PARAMS
    }

    override fun getButtonsAtRight(): Array<AppBarButtonParam>? {
        return arrayOf(AppBarButtonParam(AUTH_PHONE_NEXT_BUTTON_TAG, NOT_USED_PARAMETER, R.string.next))
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