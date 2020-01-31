package com.sudox.design.resizableImageButton

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class ResizableImageButtonState : View.BaseSavedState {

    private var iconDrawableRes = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        iconDrawableRes = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(iconDrawableRes)
    }

    fun writeFromView(button: ResizableImageButton) {
        iconDrawableRes = button.iconDrawableRes
    }

    fun readToView(button: ResizableImageButton) {
        if (iconDrawableRes != 0) {
            button.setIconDrawable(iconDrawableRes)
        }
    }
}