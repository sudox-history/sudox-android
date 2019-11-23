package com.sudox.design.navigationBar.navigationBarButton

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.core.os.ParcelCompat.readBoolean
import androidx.core.os.ParcelCompat.writeBoolean

class NavigationBarButtonState : View.BaseSavedState {

    private var clicked = false
    private var titleId = 0
    private var iconId = 0
    private var tag = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        clicked = readBoolean(source)
        titleId = source.readInt()
        iconId = source.readInt()
        tag = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)

        writeBoolean(out, clicked)
        out.writeInt(titleId)
        out.writeInt(iconId)
        out.writeInt(tag)
    }

    fun writeFromView(navigationBarButton: NavigationBarButton) = navigationBarButton.let {
        clicked = it.isClicked()
        titleId = it.titleId
        iconId = it.iconId
        tag = it.tag as Int
    }

    fun readToView(navigationBarButton: NavigationBarButton) {
        navigationBarButton.set(titleId, iconId, clicked)
        navigationBarButton.tag = tag
    }

    companion object CREATOR : Parcelable.Creator<NavigationBarButtonState> {
        override fun createFromParcel(source: Parcel): NavigationBarButtonState {
            return NavigationBarButtonState(source)
        }

        override fun newArray(size: Int): Array<NavigationBarButtonState?> {
            return arrayOfNulls(size)
        }
    }
}