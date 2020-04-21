package ru.sudox.android.people.activitytab.vos

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import ru.sudox.design.popup.vos.PopupItemVO
import ru.sudox.design.popup.vos.PopupItemWithoutIconVO
import ru.sudox.design.viewlist.vos.ViewListHeaderVO
import ru.sudox.android.people.activitytab.R
import ru.sudox.android.people.activitytab.adapters.NEWS_HEADER_TYPE

/**
 * ViewObject для секции моментов
 */
class NewsHeaderVO : ViewListHeaderVO {

    override var type: Int = NEWS_HEADER_TYPE

    constructor() : super()
    constructor(source: Parcel) : super(source)

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

    companion object CREATOR : Parcelable.Creator<ViewListHeaderVO> {
        override fun createFromParcel(source: Parcel): ViewListHeaderVO {
            return NewsHeaderVO(source)
        }

        override fun newArray(size: Int): Array<ViewListHeaderVO?> {
            return arrayOfNulls(size)
        }
    }
}