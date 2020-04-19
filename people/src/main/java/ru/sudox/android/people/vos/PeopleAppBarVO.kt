package ru.sudox.android.people.vos

import android.content.Context
import android.view.View
import ru.sudox.android.people.activitytab.R
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.others.AppBarButtonParam
import ru.sudox.design.appbar.vos.others.NOT_USED_PARAMETER

const val PEOPLE_NOTIFICATION_BUTTON_TAG = 2
const val PEOPLE_SEARCH_BUTTON_TAG = 3

class PeopleAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<AppBarButtonParam>? {
        return arrayOf(AppBarButtonParam(PEOPLE_NOTIFICATION_BUTTON_TAG, R.drawable.ic_notifications_none, NOT_USED_PARAMETER))
    }

    override fun getButtonsAtRight(): Array<AppBarButtonParam>? {
        return arrayOf(AppBarButtonParam(PEOPLE_SEARCH_BUTTON_TAG, R.drawable.ic_search, NOT_USED_PARAMETER))
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