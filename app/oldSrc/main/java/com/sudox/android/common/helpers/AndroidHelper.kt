package com.sudox.android.common.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.text.TextUtils
import com.sudox.android.data.models.contacts.dto.ContactPairDTO

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

/**
 * Загружает из телефонной книжки контакты, которые соответствуют следующим требованиям:
 *
 * 1) Задан номер телефона
 * 2) В номере телефона нет лишних символов
 * 3) Если имя контакта соответствует требованиям, то устанавливается это имя, в противном случае
 * ставится имя, которое контакт указал в Sudox.
 */
fun Context.loadContactsFromPhone(): ArrayList<ContactPairDTO> {
    val contactsPairs = ArrayList<ContactPairDTO>()
    val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
    val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null)

    // Load data ...
    cursor?.use {
        val phoneColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val displayNameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val phone = cursor.getString(phoneColumnIndex)

            // Optimization: no needed parse it contact if phone empty
            if (TextUtils.isEmpty(phone)) {
                continue
            }

            val formattedPhone = formatPhone(phone, false)

            // Optimization: no needed parse it contact if phone doesn't match the format or already added
            if (TextUtils.isEmpty(phone) || contactsPairs.find { it.phone == formattedPhone } != null) {
                continue
            }

            val displayName = cursor.getString(displayNameColumnIndex)
            var contactName = if (!TextUtils.isEmpty(displayName)) {
                displayName
                        .trim()
                        .replace(WHITESPACES_REGEX, " ")
            } else ""

            // Check name format
            if (!NAME_REGEX.matches(contactName)) {
                contactName = ""
            }

            // Add new pair
            contactsPairs.plusAssign(ContactPairDTO().apply {
                this.name = contactName
                this.phone = formattedPhone
            })
        }
    }

    // Return result
    return contactsPairs
}