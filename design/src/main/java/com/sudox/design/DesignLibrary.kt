package com.sudox.design

import android.content.Context
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

var phoneNumberUtil: PhoneNumberUtil? = null
var regionsFlags = hashMapOf(
        "RU" to R.drawable.ic_flag_russia
)

fun initDesign(context: Context) {
    phoneNumberUtil = PhoneNumberUtil.createInstance(context)
}