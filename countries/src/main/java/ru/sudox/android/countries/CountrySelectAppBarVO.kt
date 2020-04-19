package ru.sudox.android.countries

import android.content.Context
import android.view.View
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.APPBAR_BACK_BUTTON_PARAMS
import ru.sudox.design.appbar.vos.others.AppBarButtonParam
import ru.sudox.design.appbar.vos.others.NOT_USED_PARAMETER

const val COUNTRY_SELECT_SEARCH_BUTTON_TAG = 2

class CountrySelectAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<AppBarButtonParam>? {
        return APPBAR_BACK_BUTTON_PARAMS
    }

    override fun getButtonsAtRight(): Array<AppBarButtonParam>? {
        return arrayOf(AppBarButtonParam(COUNTRY_SELECT_SEARCH_BUTTON_TAG, R.drawable.ic_search, NOT_USED_PARAMETER, true))
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