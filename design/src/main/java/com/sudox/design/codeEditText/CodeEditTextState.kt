package com.sudox.design.codeEditText

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class CodeEditTextState : View.BaseSavedState {

    private var digitEditTextsIds: IntArray? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        digitEditTextsIds = source.createIntArray()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeIntArray(digitEditTextsIds)
    }

    fun writeFromView(codeEditText: CodeEditText) {
        digitEditTextsIds = IntArray(codeEditText.digitsEditTexts!!.size) {
            codeEditText.digitsEditTexts!![it].id
        }
    }

    fun readToView(codeEditText: CodeEditText) {
        digitEditTextsIds!!.forEachIndexed { index, id ->
            codeEditText.digitsEditTexts!![index].id = id
        }
    }

    companion object CREATOR : Parcelable.Creator<CodeEditTextState> {
        override fun createFromParcel(parcel: Parcel): CodeEditTextState {
            return CodeEditTextState(parcel)
        }

        override fun newArray(size: Int): Array<CodeEditTextState?> {
            return arrayOfNulls(size)
        }
    }
}