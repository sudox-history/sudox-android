package com.sudox.messenger.android.auth.code

import android.content.Context
import android.view.View
import com.sudox.design.appbar.vos.AppBarVO
import com.sudox.design.appbar.vos.BACK_BUTTON
import com.sudox.messenger.android.auth.R

class AuthCodeAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<Triple<Int, Int, Int>>? {
        return BACK_BUTTON
    }

    override fun getButtonsAtRight(): Array<Triple<Int, Int, Int>>? {
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