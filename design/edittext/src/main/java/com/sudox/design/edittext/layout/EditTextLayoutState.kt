package com.sudox.design.edittext.layout

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.sudox.design.saveableview.SaveableViewState

class EditTextLayoutState : SaveableViewState<EditTextLayout> {

    private var childId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        childId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(childId)
    }

    override fun readFromView(view: EditTextLayout) {
        childId = (view.childView as View).id
    }

    override fun writeToView(view: EditTextLayout) {
        (view.childView as View).id = childId
    }
}