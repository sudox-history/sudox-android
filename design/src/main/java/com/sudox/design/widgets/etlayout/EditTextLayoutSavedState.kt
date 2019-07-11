package com.sudox.design.widgets.etlayout

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class EditTextLayoutSavedState : View.BaseSavedState {

    internal var errorText: String? = null
    internal var errorTextRes: Int = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        errorText = source.readString()
        errorTextRes = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)

        out.writeString(errorText)
        out.writeInt(errorTextRes)
    }

    fun readFromView(editTextLayout: EditTextLayout) {
        errorText = editTextLayout.label?.errorText
        errorTextRes = editTextLayout.label?.errorTextRes ?: 0
    }

    fun writeToView(editTextLayout: EditTextLayout) {
        if (errorTextRes != 0) {
            editTextLayout.setErrorTextRes(errorTextRes)
        } else if (errorText != null) {
            editTextLayout.setErrorText(errorText)
        }
    }

    companion object CREATOR : Parcelable.Creator<EditTextLayoutSavedState> {
        override fun createFromParcel(parcel: Parcel): EditTextLayoutSavedState {
            return EditTextLayoutSavedState(parcel)
        }

        override fun newArray(size: Int): Array<EditTextLayoutSavedState?> {
            return arrayOfNulls(size)
        }
    }
}