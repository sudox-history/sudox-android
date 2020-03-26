package com.sudox.messenger.android.people.peopletab.vos.appbar

import android.content.Context
import android.view.View
import com.sudox.design.appbar.vos.AppBarVO
import com.sudox.design.appbar.vos.NOT_USED_PARAMETER
import com.sudox.messenger.android.people.peopletab.R

const val SEARCH_BUTTON_TAG = 3

class PeopleTabAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<Triple<Int, Int, Int>>? {
        return null
    }

    override fun getButtonsAtRight(): Array<Triple<Int, Int, Int>>? {
        return arrayOf(Triple(SEARCH_BUTTON_TAG, R.drawable.ic_search, NOT_USED_PARAMETER))
    }

    override fun getViewAtLeft(context: Context): View? {
        return null
    }

    override fun getViewAtRight(context: Context): View? {
        return null
    }

    override fun getTitle(): Int {
        return R.string.people
    }
}