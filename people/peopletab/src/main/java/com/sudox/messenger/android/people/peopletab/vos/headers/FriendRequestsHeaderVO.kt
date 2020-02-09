package com.sudox.messenger.android.people.peopletab.vos.headers

import android.content.Context
import android.graphics.drawable.Drawable
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.peopletab.R

class FriendRequestsHeaderVO : ViewListHeaderVO {

    override val isItemsHidden: Boolean = false
    override val selectedFunctionButtonToggleIndex = 0
    override val selectedToggleIndex = 0

    override fun getToggleOptions(context: Context): Array<Pair<Int, Pair<String, Drawable?>>> {
        return arrayOf(Pair(0, Pair(context.getString(R.string.friend_requests), null)))
    }

    override fun getFunctionButton(context: Context): Pair<Int, Drawable>? {
        return null
    }

    override fun getFunctionButtonToggleOptions(context: Context): Array<Pair<Int, Pair<String, Drawable>>>? {
        return null
    }

    override fun canHideItems(): Boolean {
        return false
    }
}