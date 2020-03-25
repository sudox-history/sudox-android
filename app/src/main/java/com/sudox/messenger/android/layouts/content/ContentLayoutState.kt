package com.sudox.messenger.android.layouts.content

import android.os.Parcel
import android.os.Parcelable
import com.sudox.design.saveableview.SaveableViewState

class ContentLayoutState : SaveableViewState<ContentLayout> {

    private var layoutChildId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        layoutChildId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(layoutChildId)
    }

    override fun readFromView(view: ContentLayout) {
        layoutChildId = view.layoutChild.id
    }

    override fun writeToView(view: ContentLayout) {
        view.layoutChild.id = layoutChildId
    }
}