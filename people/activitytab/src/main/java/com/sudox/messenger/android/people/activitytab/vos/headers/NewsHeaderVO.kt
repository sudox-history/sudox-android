package com.sudox.messenger.android.people.activitytab.vos.headers

import android.content.Context
import com.sudox.design.popup.vos.PopupItemVO
import com.sudox.design.popup.vos.PopupItemWithoutIconVO
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.R
import com.sudox.messenger.android.people.activitytab.adapters.NEWS_HEADER_TYPE

/**
 * ViewObject для секции моментов
 */
class NewsHeaderVO : ViewListHeaderVO() {

    override var type: Int = NEWS_HEADER_TYPE

    override fun getToggleOptions(context: Context): List<PopupItemVO<*>> {
        return listOf(PopupItemWithoutIconVO(0, context.getString(R.string.news), selectedToggleTag == 0))
    }

    override fun getFunctionButtonIconId(): Int {
        return 0
    }

    override fun getFunctionButtonToggleOptions(context: Context): List<PopupItemVO<*>>? {
        return null
    }

    override fun canSortItems(): Boolean {
        return false
    }

    override fun canHideItems(): Boolean {
        return false
    }
}