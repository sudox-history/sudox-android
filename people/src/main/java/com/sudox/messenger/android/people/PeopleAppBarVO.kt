package com.sudox.messenger.android.people

import android.content.Context
import android.view.View
import com.sudox.design.appbar.AppBarVO

object PeopleAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<Triple<Int, Int, Int>>? {
        return null
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
        return R.string.people
    }
}