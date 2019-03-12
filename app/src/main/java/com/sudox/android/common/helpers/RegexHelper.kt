package com.sudox.android.common.helpers

import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString

val PHONE_REGEX by lazy { "7([0-9]{10})".toRegex() }
val SMS_CODE_MESSAGE_REGEX by lazy { "^Sudox: [0-9]{5}$".toRegex() }
val NICKNAME_REGEX by lazy { "^[-a-zA-Z0-9.]{1,15}$".toRegex() }
val WHITESPACES_REGEX by lazy { "  +".toRegex() }
val NEW_LINE_ON_START_REGEX by lazy { " \n".toRegex(RegexOption.DOT_MATCHES_ALL) }
val NEW_LINE_ON_END_REGEX by lazy { "\n ".toRegex(RegexOption.DOT_MATCHES_ALL) }
val NEW_LINE_MULTIPLE_REGEX by lazy { "(\n){3,}".toRegex(RegexOption.DOT_MATCHES_ALL) }

@Deprecated("will be removed and replaced by PHONE_REGEX")
val NUMBER_REGEX by lazy { "^[0-9]+".toRegex() }

@Deprecated("will be removed")
val NAME_REGEX by lazy { "^[-a-zA-Zа-яА-Я]{1,20}[ ]?[-a-zA-Zа-яА-Я]{1,20}$".toRegex() }

/**
 * Форматирует текст сообщения.
 *
 * 1) Убирает пробелы в начале и конце сообщения.
 * 2) Убирает лишние начала строк.
 * 3) Убирает начала строк в начале и конце сообщения.
 *
 * @param message - текст сообщения.
 */
fun formatMessage(message: String): String {
    return message
            .trim()
            .replace(WHITESPACES_REGEX, " ")
            .replace(NEW_LINE_ON_START_REGEX, "\n")
            .replace(NEW_LINE_ON_END_REGEX, "\n")
            .replace(NEW_LINE_MULTIPLE_REGEX, "\n\n")
}

/**
 * Форматирует номер телефона.
 *
 * @param phone - номер телефона
 * @param includePlus - оставлять ли плюс в начале номера?
 */
fun formatPhone(phone: String, includePlus: Boolean): String {
    val allowedChars = if (includePlus) "0123456789+" else "0123456789"
    val builder = StringBuilder(phone)
    var index = 0

    // Remove unused symbols
    while (index < builder.length) {
        if ((index > 0 && builder[index] == '+') || (!allowedChars.contains(builder[index]))) {
            builder.deleteCharAt(index)
            index--
        }

        index++
    }

    return builder.toString()
}

fun formatPhoneByMask(phoneNumber: String): String {
    val mask = Mask("+7 ([000]) [000]-[00]-[00]")
    val result = mask.apply(CaretString(
            phoneNumber.substring(1, phoneNumber.length),
            phoneNumber.length), true)

    return result.formattedText.string
}