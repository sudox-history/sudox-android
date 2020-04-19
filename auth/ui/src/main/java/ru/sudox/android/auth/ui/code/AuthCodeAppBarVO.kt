package ru.sudox.android.auth.ui.code

import android.content.Context
import android.view.View
import ru.sudox.android.auth.ui.R
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.APPBAR_BACK_BUTTON_PARAMS
import ru.sudox.design.appbar.vos.others.AppBarButtonParam

class AuthCodeAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<AppBarButtonParam>? {
        return APPBAR_BACK_BUTTON_PARAMS
    }

    override fun getButtonsAtRight(): Array<AppBarButtonParam>? {
        return null
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