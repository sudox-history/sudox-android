package com.sudox.design.nicknameEditText

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class NicknameEditTextState : View.BaseSavedState {

    private var tag: String? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        tag = source.readString()
    }

    fun writeFromView(nicknameEditText: NicknameEditText) {
        tag = nicknameEditText.getNicknameTag()
    }

    fun readToView(nicknameEditText: NicknameEditText) {
        nicknameEditText.setNicknameTag(tag)
    }

    companion object CREATOR : Parcelable.Creator<NicknameEditTextState> {
        override fun createFromParcel(parcel: Parcel): NicknameEditTextState {
            return NicknameEditTextState(parcel)
        }

        override fun newArray(size: Int): Array<NicknameEditTextState?> {
            return arrayOfNulls(size)
        }
    }
}