package ru.sudox.design.codeedittext

import android.os.Parcel
import android.os.Parcelable
import ru.sudox.design.saveableview.SaveableViewState

class CodeEditTextState : SaveableViewState<CodeEditText> {

    private var digitEditTextsIds: IntArray? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        digitEditTextsIds = source.createIntArray()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeIntArray(digitEditTextsIds)
    }

    override fun readFromView(view: CodeEditText) {
        digitEditTextsIds = IntArray(view.digitsCount) {
            view.digitEditTexts!![it].id
        }
    }

    override fun writeToView(view: CodeEditText) {
        digitEditTextsIds?.forEachIndexed { index, id ->
            view.digitEditTexts!![index].id = id
        }
    }
}