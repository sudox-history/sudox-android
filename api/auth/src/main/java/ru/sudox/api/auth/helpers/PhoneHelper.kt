package ru.sudox.api.auth.helpers

import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

/**
 * Проверяет валидность номера телефона.
 *
 * @param phone Номер телефона
 * @return True если номер телефона валиден, False - если не валиден.
 */
fun PhoneNumberUtil.isPhoneNumberValid(phone: String): Boolean {
    return try {
        isValidNumber(parse(phone, null))
    } catch (e: NumberParseException) {
        false
    }
}