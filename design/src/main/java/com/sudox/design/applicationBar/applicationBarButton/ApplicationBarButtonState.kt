package com.sudox.design.applicationBar.applicationBarButton

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class ApplicationBarButtonState : View.BaseSavedState {

    private var iconDrawableId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        iconDrawableId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(iconDrawableId)
    }

    fun writeFromView(applicationBarButton: ApplicationBarButton) {
        iconDrawableId = applicationBarButton.iconDrawableId
    }

    fun readToView(applicationBarButton: ApplicationBarButton) {
        if (iconDrawableId != 0) {
            applicationBarButton.toggle(iconDrawableId)
        }
    }

    companion object CREATOR : Parcelable.Creator<ApplicationBarButtonState> {
        override fun createFromParcel(source: Parcel): ApplicationBarButtonState {
            return ApplicationBarButtonState(source)
        }

        override fun newArray(size: Int): Array<ApplicationBarButtonState?> {
            return arrayOfNulls(size)
        }
    }
}