package com.sudox.design

import android.content.Context
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

var phoneNumberUtil: PhoneNumberUtil? = null

fun initDesign(context: Context) {
    phoneNumberUtil = PhoneNumberUtil.createInstance(context)
}