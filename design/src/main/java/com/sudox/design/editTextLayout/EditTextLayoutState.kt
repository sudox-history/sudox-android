package com.sudox.design.editTextLayout

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class EditTextLayoutState : View.BaseSavedState {

    private var errorTextId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        errorTextId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(errorTextId)
    }

    fun writeFromView(editTextLayout: EditTextLayout) {
        errorTextId = editTextLayout.errorTextId
    }

    fun readToView(editTextLayout: EditTextLayout) {
        if (errorTextId != 0) {
            editTextLayout.setErrorText(errorTextId)
        } else {
            editTextLayout.setErrorText(null)
        }
    }

    companion object CREATOR : Parcelable.Creator<EditTextLayoutState> {
        override fun createFromParcel(parcel: Parcel): EditTextLayoutState {
            return EditTextLayoutState(parcel)
        }

        override fun newArray(size: Int): Array<EditTextLayoutState?> {
            return arrayOfNulls(size)
        }
    }
}