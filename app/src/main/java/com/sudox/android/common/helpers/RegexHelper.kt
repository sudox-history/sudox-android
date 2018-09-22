package com.sudox.android.common.helpers

val EMAIL_REGEX = "^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*\$"
        .toRegex()

val NUMBER_REGEX = "^[0-9]+".toRegex()
val NAME_REGEX = "^[-a-zA-Zа-яА-Я]{1,20}[ ]?[-a-zA-Zа-яА-Я]{1,20}$".toRegex()
val NICKNAME_REGEX = "^[-a-zA-Z0-9.]{1,20}$".toRegex()
val WHITESPACES_REMOVE_REGEX = " +".toRegex()