package com.sudox.design.phoneEditText

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class PhoneEditTextState : View.BaseSavedState {

    private var regionCode: String? = null
    private var countryCode = 0
    private var editTextId = 0
    private var flagDrawableResId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        regionCode = source.readString()
        countryCode = source.readInt()
        editTextId = source.readInt()
        flagDrawableResId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeString(regionCode)
        out.writeInt(countryCode)
        out.writeInt(editTextId)
        out.writeInt(flagDrawableResId)
    }

    fun writeFromView(phoneEditText: PhoneEditText) {
        editTextId = phoneEditText.numberEditText.id
        regionCode = phoneEditText.getRegionCode()
        countryCode = phoneEditText.countryCodeSelector.get()
        flagDrawableResId = phoneEditText.countryCodeSelector.flagDrawableResId
    }

    fun readToView(phoneEditText: PhoneEditText) {
        phoneEditText.numberEditText.id = editTextId

        if (regionCode != null) {
            phoneEditText.setCountry(regionCode!!, countryCode, flagDrawableResId, false)
        }
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