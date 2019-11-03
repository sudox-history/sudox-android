package com.sudox.design.applicationBar.applicationBarButton

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.core.os.ParcelCompat.readBoolean
import androidx.core.os.ParcelCompat.writeBoolean

class ApplicationBarButtonState : View.BaseSavedState {

    private var visibility: Int = 0
    private var isClickable: Boolean = false
    private var iconDirection: ApplicationBarButtonIconDirection? = null
    private var iconDrawableRes: Int = 0
    private var textRes: Int = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        visibility = source.readInt()
        isClickable = readBoolean(source)
        iconDirection = ApplicationBarButtonIconDirection.values()[source.readInt()]
        iconDrawableRes = source.readInt()
        textRes = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)

        out.let {
            it.writeInt(visibility)
            writeBoolean(it, isClickable)
            it.writeInt(iconDirection!!.ordinal)
            it.writeInt(iconDrawableRes)
            it.writeInt(textRes)
        }
    }

    fun writeFromView(button: ApplicationBarButton) {
        visibility = button.visibility
        isClickable = button.isClickable
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
        button.isClickable = isClickable
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