package ru.sudox.api

import android.content.Context
import ru.sudox.api.exceptions.ApiException
import java.io.IOException

const val OK_ERROR_CODE = 0
const val SERVICE_UNAVAILABLE_ERROR_CODE = 1
const val ACCESS_DENIED_ERROR_CODE = 2
const val REQUEST_FORMAT_INVALID_ERROR_CODE = 3
const val AUTH_SESSION_INVALID_ERROR_CODE = 101
const val AUTH_SESSION_NO_LONGER_VALID_ERROR_CODE = 102
const val AUTH_DOES_NOT_EXIST_ERROR_CODE = 103
const val AUTH_ALREADY_EXISTS_ERROR_CODE = 104
const val AUTH_TYPE_INVALID_ERROR_CODE = 105
const val AUTH_CODE_INVALID_ERROR_CODE = 106
const val AUTH_CODE_ALREADY_SENT_ERROR_CODE = 107
const val USER_PHONE_BANNED_ERROR_CODE = 108
const val USER_KEY_HASH_INVALID_ERROR_CODE = 109
const val NO_INTERNET_CONNECTION_ERROR_CODE = Int.MAX_VALUE

val errorNames = hashMapOf(
        SERVICE_UNAVAILABLE_ERROR_CODE to R.string.service_unavailable,
        ACCESS_DENIED_ERROR_CODE to R.string.access_denied,
        REQUEST_FORMAT_INVALID_ERROR_CODE to R.string.request_format_invalid,
        AUTH_SESSION_INVALID_ERROR_CODE to R.string.auth_session_invalid,
        AUTH_SESSION_NO_LONGER_VALID_ERROR_CODE to R.string.auth_session_no_longer_valid,
        AUTH_DOES_NOT_EXIST_ERROR_CODE to R.string.auth_does_not_exists,
        AUTH_ALREADY_EXISTS_ERROR_CODE to R.string.auth_already_exists,
        AUTH_TYPE_INVALID_ERROR_CODE to R.string.auth_type_invalid,
        AUTH_CODE_INVALID_ERROR_CODE to R.string.auth_code_invalid,
        AUTH_CODE_ALREADY_SENT_ERROR_CODE to R.string.auth_code_already_sent,
        USER_PHONE_BANNED_ERROR_CODE to R.string.user_phone_banned,
        USER_KEY_HASH_INVALID_ERROR_CODE to R.string.user_key_hash_invalid,
        NO_INTERNET_CONNECTION_ERROR_CODE to R.string.no_internet_connection
)

/**
 * Создает обработчик для ошибок.
 * Преобразовывает исключение в код ошибки.
 *
 * @param callback Кэллбэк для передачи кода ошибки.
 */
inline fun createApiErrorsCallback(crossinline callback: (Int) -> (Unit)): (Throwable) -> (Unit) = {
    if (it is ApiException) {
        callback(it.code)
    } else if (it is IOException) {
        callback(NO_INTERNET_CONNECTION_ERROR_CODE)
    }
}

/**
 * Выдает текст ошибки по её коду.
 *
 * @param context Контекст приложения/активности
 * @param code Код ошибки
 * @return Текст ошибки
 */
fun getErrorText(context: Context, code: Int): String {
    return context.getString(errorNames[code]!!)
}