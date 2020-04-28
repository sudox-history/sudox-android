package ru.sudox.api.auth

internal val USERNAME_REGEX = "^([A-zА-я]{1,20}\\s?){2}\$".toRegex()
internal val USERNICKNAME_REGEX = "^[0-9A-z.:_\\-()]{1,30}\$".toRegex()