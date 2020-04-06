package ru.sudox.android.messages.vos

import android.content.Context
import android.view.View
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.NOT_USED_PARAMETER
import ru.sudox.android.messages.R

const val DIALOGS_SEARCH_BUTTON_TAG = 2

class DialogsAppBarVO : AppBarVO {

    override fun getButtonsAtLeft(): Array<Triple<Int, Int, Int>>? {
        return null
    }

    override fun getButtonsAtRight(): Array<Triple<Int, Int, Int>>? {
        return arrayOf(Triple(DIALOGS_SEARCH_BUTTON_TAG, R.drawable.ic_search, NOT_USED_PARAMETER))
    }

    override fun getViewAtLeft(context: Context): View? {
        return null
    }

    override fun getViewAtRight(context: Context): View? {
        return null
    }

    override fun getTitle(): Int {
        return R.string.messages
    }
}