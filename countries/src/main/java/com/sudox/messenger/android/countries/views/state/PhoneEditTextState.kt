package com.sudox.messenger.android.countries.views.state

import android.os.Parcel
import android.os.Parcelable
import com.sudox.design.saveableview.SaveableViewState
import com.sudox.messenger.android.countries.views.PhoneEditText
import com.sudox.messenger.android.countries.vos.CountryVO

class PhoneEditTextState : SaveableViewState<PhoneEditText> {

    private var countryVO: CountryVO? = null
    private var editTextId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        countryVO = source.readParcelable(CountryVO::class.java.classLoader)
        editTextId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)

        out.let {
            it.writeParcelable(countryVO, 0)
            it.writeInt(editTextId)
        }
    }

    override fun readFromView(view: PhoneEditText) {
        countryVO = view.vo
        editTextId = view.editText.id
    }

    override fun writeToView(view: PhoneEditText) {
        view.vo = countryVO
        view.editText.id = editTextId
    }
}