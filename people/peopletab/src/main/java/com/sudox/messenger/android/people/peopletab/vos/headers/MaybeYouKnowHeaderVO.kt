package com.sudox.messenger.android.people.peopletab.vos.headers

import android.content.Context
import android.graphics.drawable.Drawable
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.peopletab.R

class MaybeYouKnowHeaderVO(
        override val isItemsHidden: Boolean = false
) : ViewListHeaderVO {

    override val selectedFunctionButtonToggleIndex = 0
    override val selectedToggleIndex = 0

    override fun getToggleOptions(context: Context): Array<Pair<Int, Pair<String, Drawable?>>> {
        return arrayOf(Pair(0, Pair(context.getString(R.string.maybe_you_know), null)))
    }

    override fun getFunctionButton(context: Context): Pair<Int, Drawable>? {
        return null
    }

    override fun getFunctionButtonToggleOptions(context: Context): Array<Pair<Int, Pair<String, Drawable>>>? {
        return null
    }

    override fun canHideItems(): Boolean {
        return true
    }
}