package com.sudox.messenger.android.people.peopletab.vos.headers

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.SparseIntArray
import com.sudox.design.popup.vos.PopupItemVO
import com.sudox.design.popup.vos.PopupItemWithoutIconVO
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.peopletab.R
import com.sudox.messenger.android.people.peopletab.adapters.MAYBE_YOU_KNOW_HEADER_TYPE

class MaybeYouKnowHeaderVO(
        override var isItemsHidden: Boolean = false
) : ViewListHeaderVO {

    override var type: Int = MAYBE_YOU_KNOW_HEADER_TYPE
    override var selectedFunctionButtonToggleTags: SparseIntArray? = null
    override var isContentLoading: Boolean = false
    override var selectedToggleTag = 0

    override fun getToggleOptions(context: Context): List<PopupItemVO<*>> {
        return listOf(PopupItemWithoutIconVO(0, context.getString(R.string.maybe_you_know), selectedToggleTag == 0))
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