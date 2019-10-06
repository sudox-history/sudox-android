package com.sudox.design.phoneEditText

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class PhoneEditTextState : View.BaseSavedState {

    private var regionCode: String? = null
    private var countryCodeSelectorId = 0
    private var editTextId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        regionCode = source.readString()
        countryCodeSelectorId = source.readInt()
        editTextId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeString(regionCode)
        out.writeInt(countryCodeSelectorId)
        out.writeInt(editTextId)
    }

    fun writeFromView(phoneEditText: PhoneEditText) {
        editTextId = phoneEditText.numberEditText.id
        countryCodeSelectorId = phoneEditText.countryCodeSelector.id
        regionCode = phoneEditText.getRegionCode()
    }

    fun readToView(phoneEditText: PhoneEditText) {
        if (regionCode != null) {
            phoneEditText.phoneTextWatcher.setRegionCode(regionCode!!)
        }

        phoneEditText.numberEditText.id = editTextId
        phoneEditText.countryCodeSelector.id = countryCodeSelectorId
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