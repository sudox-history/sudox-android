package ru.sudox.api.auth

val NAME_REGEX = "^([A-zА-я]{1,20}\\s?){2}\$".toRegex()
val NICKNAME_REGEX = "^[0-9A-z.:_\\-()]{1,30}\$".toRegex()