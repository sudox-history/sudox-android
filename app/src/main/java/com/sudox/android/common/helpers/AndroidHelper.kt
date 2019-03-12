package com.sudox.android.common.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Открывает менеджер SMS-сообщений с уже вбитым сообщением и номером для отправки.
 *
 * @param phone - номер телефона
 * @param message - текст сообщения.
 */
fun Context.sendMessageViaSms(phone: String, message: String) {
    startActivity(Intent(Intent.ACTION_VIEW).apply {
        type = "vnd.android-dir/mms-sms"
        putExtra("address", phone)
        putExtra("sms_body", message)
    })
}