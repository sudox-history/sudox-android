package com.sudox.android.common.helpers

import android.content.Context
import android.content.Intent

fun Context.sendSmsMessage(phone: String, body: String) {
    val smsIntent = Intent(Intent.ACTION_VIEW).apply {
        type = "vnd.android-dir/mms-sms"
        putExtra("address", phone)
        putExtra("sms_body", body)
    }

    startActivity(smsIntent)
}