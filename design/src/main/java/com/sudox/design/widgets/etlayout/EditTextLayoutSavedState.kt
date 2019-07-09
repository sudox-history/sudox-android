package com.sudox.design.widgets.etlayout

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class EditTextLayoutSavedState : View.BaseSavedState {

    internal var originalText: String? = null
    internal var errorText: String? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        originalText = source.readString()
        errorText = source.readString()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)

        out.apply {
            writeString(originalText)
            writeString(errorText)
        }
    }

    fun readFromView(editTextLayout: EditTextLayout) {
        originalText = editTextLayout.label?.originalText
        errorText = editTextLayout.label?.errorText
    }

    fun writeToView(editTextLayout: EditTextLayout) {
        editTextLayout.label!!.originalText = originalText
        editTextLayout.label!!.errorText = errorText
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