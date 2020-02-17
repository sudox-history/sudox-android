package com.sudox.messenger.android.people.peopletab.vos.headers

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.SparseIntArray
import com.sudox.design.popup.vos.PopupItemVO
import com.sudox.design.popup.vos.PopupItemWithoutIconVO
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.peopletab.R
import com.sudox.messenger.android.people.peopletab.adapters.FRIEND_REQUESTS_HEADER_TYPE

class FriendRequestsHeaderVO(
        override var isItemsHidden: Boolean = false,
        override var isContentLoading: Boolean = false
) : ViewListHeaderVO {

    override var type: Int = FRIEND_REQUESTS_HEADER_TYPE
    override var selectedFunctionButtonToggleTags: SparseIntArray? = null
    override var selectedToggleTag = 0

    override fun getToggleOptions(context: Context): List<PopupItemVO<*>> {
        return listOf(PopupItemWithoutIconVO(0, context.getString(R.string.friend_requests), selectedToggleTag == 0))
    }

    override fun getFunctionButtonIcon(context: Context): Drawable? {
        return null
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