package com.sudox.messenger.android.layouts

import android.os.Parcel
import android.os.Parcelable
import com.sudox.design.saveableview.SaveableViewState

class AppLayoutState : SaveableViewState<AppLayout> {

    private var contentLayoutId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        contentLayoutId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(contentLayoutId)
    }

    override fun readFromView(view: AppLayout) {
        contentLayoutId = view.contentLayout.id
    }

    override fun writeToView(view: AppLayout) {
        view.contentLayout.id = contentLayoutId
    }
}