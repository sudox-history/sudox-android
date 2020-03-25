package com.sudox.messenger.android.auth.register

import android.content.Context
import android.view.View
import com.sudox.design.appbar.AppBarVO
import com.sudox.design.appbar.BACK_BUTTON
import com.sudox.design.appbar.NOT_USED_PARAMETER
import com.sudox.messenger.android.auth.R

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