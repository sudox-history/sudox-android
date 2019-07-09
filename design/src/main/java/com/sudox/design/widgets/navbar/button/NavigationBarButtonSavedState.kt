package com.sudox.design.widgets.navbar.button

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class NavigationBarButtonSavedState : View.BaseSavedState {

    internal var iconDirection: Int = 0
    internal var iconDrawableRes: Int = 0
    internal var textRes: Int = 0
    internal var visibility: Int = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        iconDirection = source.readInt()
        iconDrawableRes = source.readInt()
        textRes = source.readInt()
        visibility = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)

        out.apply {
            writeInt(iconDirection)
            writeInt(iconDrawableRes)
            writeInt(textRes)
            writeInt(visibility)
        }
    }

    fun readFromView(button: NavigationBarButton) {
        iconDirection = button.iconDirection
        iconDrawableRes = button.iconDrawableRes
        textRes = button.textRes
        visibility = button.visibility
    }

    fun writeToView(button: NavigationBarButton) {
        if (iconDrawableRes != 0) {
            button.setIconDrawableRes(iconDrawableRes)
        }

        if (textRes != 0) {
            button.setTextRes(textRes)
        }

        button.apply {
            setIconDirection(iconDirection)
            visibility = this@NavigationBarButtonSavedState.visibility
        }
    }

    companion object CREATOR : Parcelable.Creator<NavigationBarButtonSavedState> {
        override fun createFromParcel(parcel: Parcel): NavigationBarButtonSavedState {
            return NavigationBarButtonSavedState(parcel)
        }

        override fun newArray(size: Int): Array<NavigationBarButtonSavedState?> {
            return arrayOfNulls(size)
        }
    }
}