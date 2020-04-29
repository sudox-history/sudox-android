package ru.sudox.api.common

import ru.sudox.api.common.exceptions.ApiException
import ru.sudox.api.common.exceptions.AttackSuspicionException
import java.io.IOException

const val OK_ERROR_CODE = 0
const val SERVICE_UNAVAILABLE_ERROR_CODE = 1
const val ACCESS_DENIED_ERROR_CODE = 2
const val FORMAT_INVALID_ERROR_CODE = 3
const val CLIENT_NOT_FOUND_ERROR_CODE = 4
const val SESSION_NOT_FOUND_ERROR_CODE = 4

fun getErrorText(throwable: Throwable): String {
    // TODO: Изменить тексты

    return if (throwable is AttackSuspicionException) {
        "Attack detected!"
    } else if (throwable is IOException) {
        "Connection not installed!"
    } else if (throwable is ApiException) {
        "${throwable.code}"
    } else if (BuildConfig.DEBUG && throwable.message != null) {
        throwable.message!!
    } else {
        "Error"
    }
}