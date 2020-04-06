package ru.sudox.android.people.peopletab.vos.headers

import android.content.Context
import android.os.Parcel
import ru.sudox.design.popup.vos.PopupItemVO
import ru.sudox.design.popup.vos.PopupItemWithoutIconVO
import ru.sudox.design.viewlist.vos.ViewListHeaderVO
import ru.sudox.android.people.peopletab.R
import ru.sudox.android.people.peopletab.adapters.FRIEND_REQUESTS_HEADER_TYPE

/**
 * ViewObject для шапки секции запросов в друзья.
 */
class FriendRequestsHeaderVO : ViewListHeaderVO {

    override var type = FRIEND_REQUESTS_HEADER_TYPE

    @Suppress("unused")
    constructor(source: Parcel) : super(source)
    constructor() : super()

    override fun getToggleOptions(context: Context): List<PopupItemVO<*>> {
        return listOf(PopupItemWithoutIconVO(0, context.getString(R.string.friend_requests), selectedToggleTag == 0))
    }

    override fun getFunctionButtonIconId(): Int {
        return 0
    }

    override fun getFunctionButtonToggleOptions(context: Context): List<PopupItemVO<*>>? {
        return null
    }

    override fun canHideItems(): Boolean {
        return true
    }

    override fun canSortItems(): Boolean {
        return false
    }
}