package com.sudox.android.common.helpers

val EMAIL_REGEX = "^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*\$"
        .toRegex()

val NAME_REGEX = "^[-a-zA-Zа-яА-Я]{1,20}"
        .toRegex()