package com.sudox.design.applicationBar.applicationBarButton

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class ApplicationBarButtonState : View.BaseSavedState {

    private var visibility: Int = 0
    private var clickableFlag: Byte = 0
    private var iconDirection: ApplicationBarButtonIconDirection? = null
    private var iconDrawableRes: Int = 0
    private var textRes: Int = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        visibility = source.readInt()
        clickableFlag = source.readByte()
        iconDirection = ApplicationBarButtonIconDirection.values()[source.readInt()]
        iconDrawableRes = source.readInt()
        textRes = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)

        out.apply {
            writeInt(visibility)
            writeByte(clickableFlag)
            writeInt(iconDirection!!.ordinal)
            writeInt(iconDrawableRes)
            writeInt(textRes)
        }
    }

    fun writeFromView(button: ApplicationBarButton) {
        visibility = button.visibility
        clickableFlag = if (button.isClickable) 1.toByte() else 0.toByte()
        iconDirection = button.iconDirection
        iconDrawableRes = button.iconDrawableRes
        textRes = button.textRes
    }

    fun readToView(button: ApplicationBarButton) {
        if (iconDrawableRes != 0) {
            button.setIconDrawable(iconDrawableRes)
        }

        if (textRes != 0) {
            button.setText(textRes)
        }

        button.setIconDirection(iconDirection!!)
        button.isClickable = clickableFlag == 1.toByte()
        button.visibility = visibility
    }

    companion object CREATOR : Parcelable.Creator<ApplicationBarButtonState> {
        override fun createFromParcel(parcel: Parcel): ApplicationBarButtonState {
            return ApplicationBarButtonState(parcel)
        }

        override fun newArray(size: Int): Array<ApplicationBarButtonState?> {
            return arrayOfNulls(size)
        }
    }
}