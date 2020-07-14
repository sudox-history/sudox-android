package ru.sudox.android.core.ui.phone

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * Состояния поля ввода для телефона.
 */
class PhoneEditTextState : View.BaseSavedState {

    var regionCode: String? = null

    @Suppress("unused")
    constructor(superState: Parcelable?) : super(superState)
    constructor(source: Parcel) : super(source) {
        regionCode = source.readString()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeString(regionCode)
    }

    companion object CREATOR : Parcelable.Creator<PhoneEditTextState> {
        override fun createFromParcel(parcel: Parcel): PhoneEditTextState {
            return PhoneEditTextState(parcel)
        }

        override fun newArray(size: Int): Array<PhoneEditTextState?> {
            return arrayOfNulls(size)
        }
    }
}