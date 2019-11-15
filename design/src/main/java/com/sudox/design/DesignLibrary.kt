package com.sudox.design

import android.content.Context
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

var phoneNumberUtil: PhoneNumberUtil? = null

fun loadDesignComponents(context: Context) {
    phoneNumberUtil = PhoneNumberUtil.createInstance(context)
}