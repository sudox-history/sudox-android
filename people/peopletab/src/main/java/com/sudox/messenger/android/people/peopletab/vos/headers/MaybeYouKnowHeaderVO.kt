package com.sudox.messenger.android.people.peopletab.vos.headers

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import com.sudox.design.popup.vos.PopupItemVO
import com.sudox.design.popup.vos.PopupItemWithoutIconVO
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.people.peopletab.R
import com.sudox.messenger.android.people.peopletab.adapters.MAYBE_YOU_KNOW_HEADER_TYPE

/**
 * ViewObject для шапки секции "Maybe you know".
 */
class MaybeYouKnowHeaderVO : ViewListHeaderVO {

    override var type = MAYBE_YOU_KNOW_HEADER_TYPE

    @Suppress("unused")
    constructor(source: Parcel) : super(source)
    constructor() : super()

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