package ru.sudox.android.countries.views.state

import android.os.Parcel
import android.os.Parcelable
import ru.sudox.design.saveableview.SaveableViewState
import ru.sudox.android.countries.views.PhoneEditText
import ru.sudox.android.countries.vos.CountryVO

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
        if (!view.ignoreCountryFromState) {
            view.vo = countryVO
        }

        view.editText.id = editTextId
    }
}