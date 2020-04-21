package ru.sudox.android.people.peopletab.vos.headers

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
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

    constructor() : super()
    constructor(source: Parcel) : super(source)

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

    companion object CREATOR : Parcelable.Creator<ViewListHeaderVO> {
        override fun createFromParcel(source: Parcel): ViewListHeaderVO {
            return FriendRequestsHeaderVO(source)
        }

        override fun newArray(size: Int): Array<ViewListHeaderVO?> {
            return arrayOfNulls(size)
        }
    }
}