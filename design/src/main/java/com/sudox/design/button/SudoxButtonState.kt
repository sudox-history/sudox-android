package com.sudox.design.button

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.core.os.ParcelCompat

class SudoxButtonState : View.BaseSavedState {

    private var isInLoadingState = false

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        isInLoadingState = ParcelCompat.readBoolean(source)
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        ParcelCompat.writeBoolean(out, isInLoadingState)
    }

    fun writeFromView(sudoxButton: SudoxButton) {
        isInLoadingState = sudoxButton.isInLoadingState()
    }

    fun readToView(sudoxButton: SudoxButton) {
        sudoxButton.toggleLoadingStateForce(isInLoadingState)
    }

    companion object CREATOR : Parcelable.Creator<SudoxButtonState> {
        override fun createFromParcel(parcel: Parcel): SudoxButtonState {
            return SudoxButtonState(parcel)
        }

        override fun newArray(size: Int): Array<SudoxButtonState?> {
            return arrayOfNulls(size)
        }
    }
}