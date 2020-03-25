package com.sudox.messenger.android.layouts.child

import android.os.Parcel
import android.os.Parcelable
import com.sudox.design.saveableview.SaveableViewState

class AppLayoutChildState : SaveableViewState<AppLayoutChild> {

    private var frameLayoutId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        frameLayoutId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(frameLayoutId)
    }

    override fun readFromView(view: AppLayoutChild) {
        frameLayoutId = view.frameLayout.id
    }

    override fun writeToView(view: AppLayoutChild) {
        view.frameLayout.id = frameLayoutId
    }
}