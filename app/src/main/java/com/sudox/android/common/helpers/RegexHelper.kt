package com.sudox.android.common.helpers

val EMAIL_REGEX = "^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*\$"
        .toRegex()

val NUMBER_REGEX = "^[0-9]+".toRegex()
val NAME_REGEX = "^[-a-zA-Zа-яА-Я]{1,20}[ ]?[-a-zA-Zа-яА-Я]{1,20}$".toRegex()
val NICKNAME_REGEX = "^[-a-zA-Z0-9.]{1,20}$".toRegex()
val WHITESPACES_REMOVE_REGEX = " +".toRegex()
val NEW_LINE_ON_START_REMOVE_REGEX = " .n".toRegex(RegexOption.DOT_MATCHES_ALL)
val NEW_LINE_ON_END_REMOVE_REGEX = ".n ".toRegex(RegexOption.DOT_MATCHES_ALL)
val NEW_LINE_MULTIPLE_REMOVE_REGEX = "(.n){3,}".toRegex(RegexOption.DOT_MATCHES_ALL)

fun formatMessage(message: String): String {
    return message.trim()
            .replace(WHITESPACES_REMOVE_REGEX, " ")
            .replace(NEW_LINE_ON_START_REMOVE_REGEX, "\n")
            .replace(NEW_LINE_ON_END_REMOVE_REGEX, "\n")
            .replace(NEW_LINE_MULTIPLE_REMOVE_REGEX, "\n\n")
}