package com.sudox.messenger.api.common

internal val NICKNAME_REGEX = Regex("^[-a-zA-Z0-9.!;&^\$*#()-+]{3,15}\$")
internal val PHONE_REGEX = Regex("^7[0-9]{10}\$")