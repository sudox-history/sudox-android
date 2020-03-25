package com.sudox.messenger.android.auth.country

import android.content.Context
import android.view.View
import com.sudox.design.appbar.AppBarVO
import com.sudox.design.appbar.BACK_BUTTON
import com.sudox.design.appbar.NOT_USED_PARAMETER
import com.sudox.messenger.android.auth.R

const val AUTH_COUNTRY_SEARCH_BUTTON_TAG = 2

class AuthCountryAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<Triple<Int, Int, Int>>? {
        return BACK_BUTTON
    }

    override fun getButtonsAtRight(): Array<Triple<Int, Int, Int>>? {
        return arrayOf(Triple(AUTH_COUNTRY_SEARCH_BUTTON_TAG, R.drawable.ic_search, NOT_USED_PARAMETER))
    }

    override fun getViewAtLeft(context: Context): View? {
        return null
    }

    override fun getViewAtRight(context: Context): View? {
        return null
    }

    override fun getTitle(): Int {
        return R.string.countries
    }
}