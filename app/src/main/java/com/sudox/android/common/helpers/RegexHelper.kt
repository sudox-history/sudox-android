package com.sudox.android.common.helpers

import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString

val EMAIL_REGEX = "^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*\$"
        .toRegex()

val PHONE_REGEX = "7([0-9]{10})".toRegex()
val SMS_CODE_REGEX = "^Sudox: [0-9]{5}$".toRegex()
val NUMBER_REGEX = "^[0-9]+".toRegex()
val NAME_REGEX = "^[-a-zA-Zа-яА-Я]{1,20}[ ]?[-a-zA-Zа-яА-Я]{1,20}$".toRegex()
val NICKNAME_REGEX = "^[-a-zA-Z0-9.]{1,20}$".toRegex()
val WHITESPACES_REMOVE_REGEX = " +".toRegex()
val NEW_LINE_ON_START_REMOVE_REGEX = " .n".toRegex(RegexOption.DOT_MATCHES_ALL)
val NEW_LINE_ON_END_REMOVE_REGEX = ".n ".toRegex(RegexOption.DOT_MATCHES_ALL)
val NEW_LINE_MULTIPLE_REMOVE_REGEX = "(.n){3,}".toRegex(RegexOption.DOT_MATCHES_ALL)

fun formatMessageText(message: String): String {
    // TODO. In future need be move to the AbstractMessagesRepository

    return message.trim()
            .replace(WHITESPACES_REMOVE_REGEX, " ")
            .replace(NEW_LINE_ON_START_REMOVE_REGEX, "\n")
            .replace(NEW_LINE_ON_END_REMOVE_REGEX, "\n")
            .replace(NEW_LINE_MULTIPLE_REMOVE_REGEX, "\n\n")
}

fun formatPhoneByMask(phoneNumber: String): String {
    val mask = Mask("+7 ([000]) [000]-[00]-[00]")

    val result = mask.apply(CaretString(
            phoneNumber.substring(1, phoneNumber.length),
            phoneNumber.length), true)

    return result.formattedText.string
}