package com.sudox.design.actionButton

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.core.os.ParcelCompat

class ActionButtonState : View.BaseSavedState {

    private var isLoadingState = false

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        isLoadingState = ParcelCompat.readBoolean(source)
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        ParcelCompat.writeBoolean(out, isLoadingState)
    }

    fun writeFromView(actionButton: ActionButton) {
        isLoadingState = actionButton.isLoadingState()
    }

    fun readToView(actionButton: ActionButton) {
        actionButton.toggleLoadingStateForce(isLoadingState)
    }

    companion object CREATOR : Parcelable.Creator<ActionButtonState> {
        override fun createFromParcel(parcel: Parcel): ActionButtonState {
            return ActionButtonState(parcel)
        }

        override fun newArray(size: Int): Array<ActionButtonState?> {
            return arrayOfNulls(size)
        }
    }
}