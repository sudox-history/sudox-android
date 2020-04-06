package ru.sudox.android.people.activitytab.vos.headers

import android.content.Context
import ru.sudox.design.popup.vos.PopupItemVO
import ru.sudox.design.popup.vos.PopupItemWithoutIconVO
import ru.sudox.design.viewlist.vos.ViewListHeaderVO
import ru.sudox.android.people.activitytab.R
import ru.sudox.android.people.activitytab.adapters.MOMENTS_HEADER_TYPE

/**
 * ViewObject для секции моментов
 */
class MomentsHeaderVO : ViewListHeaderVO() {

    override var type: Int = MOMENTS_HEADER_TYPE

    override fun getToggleOptions(context: Context): List<PopupItemVO<*>> {
        return listOf(PopupItemWithoutIconVO(0, context.getString(R.string.moments), selectedToggleTag == 0))
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