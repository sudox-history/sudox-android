package com.sudox.design.widgets.navbar.button

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class NavigationBarButtonSavedState : View.BaseSavedState {

    internal var visibility: Int = 0
    internal var clickableFlag: Byte = 0

    internal var iconDirection: Int = 0
    internal var iconDrawableRes: Int = 0
    internal var textRes: Int = 0
    internal var text: String? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        visibility = source.readInt()
        clickableFlag = source.readByte()
        iconDirection = source.readInt()
        iconDrawableRes = source.readInt()
        textRes = source.readInt()
        text = source.readString()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)

        out.apply {
            writeInt(visibility)
            writeByte(clickableFlag)
            writeInt(iconDirection)
            writeInt(iconDrawableRes)
            writeInt(textRes)
            writeString(text)
        }
    }

    fun readFromView(button: NavigationBarButton) {
        visibility = button.visibility
        clickableFlag = if (button.isClickable) 1.toByte() else 0.toByte()
        iconDirection = button.iconDirection
        iconDrawableRes = button.iconDrawableRes
        textRes = button.textRes
        text = button.text
    }

    fun writeToView(button: NavigationBarButton) {
        if (iconDrawableRes != 0) {
            button.setIconDrawableRes(iconDrawableRes)
        }

        if (textRes != 0) {
            button.setTextRes(textRes)
        } else if (text != null) {
            button.setText(text)
        }

        button.setIconDirection(iconDirection)
        button.isClickable = (clickableFlag == 1.toByte())
        button.visibility = visibility
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