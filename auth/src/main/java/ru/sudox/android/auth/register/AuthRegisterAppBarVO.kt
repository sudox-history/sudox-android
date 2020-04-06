package ru.sudox.android.auth.register

import android.content.Context
import android.view.View
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.BACK_BUTTON
import ru.sudox.design.appbar.vos.NOT_USED_PARAMETER
import ru.sudox.android.auth.R

const val AUTH_REGISTER_FINISH_BUTTON_TAG = 2

class AuthRegisterAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<Triple<Int, Int, Int>>? {
        return BACK_BUTTON
    }

    override fun getButtonsAtRight(): Array<Triple<Int, Int, Int>>? {
        return arrayOf(Triple(AUTH_REGISTER_FINISH_BUTTON_TAG, NOT_USED_PARAMETER, R.string.finish))
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