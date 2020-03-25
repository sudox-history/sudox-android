package com.sudox.messenger.android.layouts

import android.os.Parcel
import android.os.Parcelable
import com.sudox.design.saveableview.SaveableViewState

class AppLayoutState : SaveableViewState<AppLayout> {

    private var layoutChildId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        layoutChildId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(layoutChildId)
    }

    override fun readFromView(view: AppLayout) {
        layoutChildId = view.layoutChild.id
    }

    override fun writeToView(view: AppLayout) {
        view.layoutChild.id = layoutChildId
    }
}