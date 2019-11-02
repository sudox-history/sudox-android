package com.sudox.design.phoneEditText.childs

import android.content.Context
import android.os.Build
import android.text.InputType
import android.view.autofill.AutofillValue
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import com.sudox.design.phoneEditText.PhoneEditText
import com.sudox.design.phoneEditText.PhoneTextWatcher
import com.sudox.design.phoneNumberUtil
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

class PhoneNumberEditText(context: Context) : AppCompatEditText(context) {

    private var phoneTextWatcher = PhoneTextWatcher()

    init {
        addTextChangedListener(phoneTextWatcher)

        inputType = InputType.TYPE_CLASS_PHONE
        imeOptions = EditorInfo.IME_ACTION_DONE
        isSingleLine = true
        maxLines = 1
    }

    fun setCountry(regionCode: String, countryCode: Int) {
        phoneTextWatcher.setCountry(regionCode, countryCode)

        val exampleNumber = phoneNumberUtil!!.getExampleNumber(regionCode)
        val formattedExampleNumber = phoneNumberUtil!!
                .format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                .removePrefix("+${countryCode} ")

        hint = formattedExampleNumber
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun autofill(value: AutofillValue) {
        val phoneEditText = parent as PhoneEditText
        val phoneNumberText = value.textValue
        val phoneNumber = phoneNumberUtil!!.parse(phoneNumberText, getRegionCode())
        val regionCode = phoneNumberUtil!!.getRegionCodeForNumber(phoneNumber)
        val regionFlagId = if (phoneEditText.getRegionCode() == regionCode) {
            phoneEditText.countryCodeSelector.flagDrawableResId
        } else {
            phoneEditText.regionFlagIdCallback?.invoke(regionCode) ?: return
        }

        if (regionFlagId != 0) {
            phoneEditText.setCountry(regionCode, phoneNumber.countryCode, regionFlagId)

            setText(phoneNumberText.removePrefix("+${phoneNumber.countryCode}"))
            setSelection(text!!.length)
        }
    }

    fun getRegionCode(): String? {
        return phoneTextWatcher.countryNameCode
    }
}