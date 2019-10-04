package com.sudox.design.phoneEditText.countryCodeSelector

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class CountryCodeSelectorState : View.BaseSavedState {

    private var flagDrawableResId = 0
    private var code: String? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        flagDrawableResId = source.readInt()
        code = source.readString()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(flagDrawableResId)
        out.writeString(code)
    }

    fun writeFromView(countryCodeSelector: CountryCodeSelector) {
        flagDrawableResId = countryCodeSelector.flagDrawableResId
        code = countryCodeSelector.get()
    }

    fun readToView(countryCodeSelector: CountryCodeSelector) {
        if (code != null) {
            countryCodeSelector.set(code!!, flagDrawableResId)
        }
    }

    companion object CREATOR : Parcelable.Creator<CountryCodeSelectorState> {
        override fun createFromParcel(parcel: Parcel): CountryCodeSelectorState {
            return CountryCodeSelectorState(parcel)
        }

        override fun newArray(size: Int): Array<CountryCodeSelectorState?> {
            return arrayOfNulls(size)
        }
    }
}