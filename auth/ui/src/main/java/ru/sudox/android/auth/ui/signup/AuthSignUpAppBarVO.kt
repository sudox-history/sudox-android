package ru.sudox.android.auth.ui.signup

import android.content.Context
import android.view.View
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.APPBAR_BACK_BUTTON_PARAMS
import ru.sudox.design.appbar.vos.others.NOT_USED_PARAMETER
import ru.sudox.android.auth.ui.R
import ru.sudox.design.appbar.vos.others.AppBarButtonParam

const val AUTH_SIGN_UP_FINISH_BUTTON_TAG = 3

class AuthSignUpAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<AppBarButtonParam>? {
        return APPBAR_BACK_BUTTON_PARAMS
    }

    override fun getButtonsAtRight(): Array<AppBarButtonParam>? {
        return arrayOf(AppBarButtonParam(AUTH_SIGN_UP_FINISH_BUTTON_TAG, NOT_USED_PARAMETER, R.string.finish))
    }

    override fun getViewAtLeft(context: Context): View? {
        return null
    }

    override fun getViewAtRight(context: Context): View? {
        return null
    }

    override fun getTitle(): Int {
        return R.string.sign_up
    }
}