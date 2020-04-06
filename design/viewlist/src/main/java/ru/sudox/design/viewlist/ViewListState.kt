package ru.sudox.design.viewlist

import android.os.Parcel
import android.os.Parcelable
import ru.sudox.design.saveableview.SaveableViewState
import ru.sudox.design.viewlist.vos.ViewListHeaderVO

class ViewListState : SaveableViewState<ViewList> {

    var headersVOs: Array<ViewListHeaderVO>? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        headersVOs = source.createTypedArray(ViewListHeaderVO.CREATOR)
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeTypedArray(headersVOs, 0)
    }

    override fun readFromView(view: ViewList) {
        (view.adapter as? ViewListAdapter<*>)?.let {
            it.saveNestedRecyclerViewsState()
            headersVOs = it.headersVOs
        }
    }

    override fun writeToView(view: ViewList) {
        (view.adapter as? ViewListAdapter<*>)?.headersVOs = headersVOs
    }
}