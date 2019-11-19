package com.sudox.design.codeEditText

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class CodeEditTextState : View.BaseSavedState {

    private var digitEditTextsIds: IntArray? = null
    private var lastFocusedDigitEditText = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        digitEditTextsIds = source.createIntArray()
        lastFocusedDigitEditText = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)

        out.let {
            it.writeIntArray(digitEditTextsIds)
            it.writeInt(lastFocusedDigitEditText)
        }
    }

    fun writeFromView(codeEditText: CodeEditText) {
        digitEditTextsIds = IntArray(codeEditText.digitsEditTexts!!.size) {
            val editText = codeEditText.digitsEditTexts!![it]

            if (editText.isFocused) {
                lastFocusedDigitEditText = it
            }

            editText.id
        }
    }

    fun readToView(codeEditText: CodeEditText) {
        digitEditTextsIds!!.forEachIndexed { index, id ->
            codeEditText.digitsEditTexts!![index].id = id
        }

        codeEditText.digitsEditTexts!![lastFocusedDigitEditText].requestFocus()
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