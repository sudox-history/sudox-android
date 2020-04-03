package com.sudox.messenger.android.countries.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.autofill.AutofillValue
import androidx.annotation.RequiresApi
import com.sudox.design.edittext.BasicEditText
import com.sudox.design.edittext.layout.EditTextLayout
import com.sudox.messenger.android.countries.COUNTRIES
import com.sudox.messenger.android.countries.R

class PhoneNumberEditText : BasicEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun autofill(value: AutofillValue) {
        (parent as PhoneEditText).let {
            val phoneNumberText = value.textValue
            val phoneNumber = it.phoneNumberUtil!!.parse(phoneNumberText, it.vo?.regionCode)
            val regionCode = it.phoneNumberUtil!!.getRegionCodeForNumber(phoneNumber)
            val countryVO = COUNTRIES[regionCode]

            if (countryVO != null) {
                it.vo = countryVO

                setText(phoneNumberText.removePrefix("+${phoneNumber.countryCode}"))
                setSelection(text!!.length)
            } else if (it.parent is EditTextLayout) {
                (it.parent as EditTextLayout).errorText = context.getString(R.string.sudox_not_working_in_this_country)
            }
        }
    }
}