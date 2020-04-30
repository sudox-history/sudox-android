package ru.sudox.android.countries.helpers

import io.michaelrocks.libphonenumber.android.NumberParseException
import ru.sudox.android.countries.R
import ru.sudox.android.countries.vos.CountryVO
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.Locale

val COUNTRIES = hashMapOf(
        createCountryPair(CountryVO("RU", R.string.russia, R.drawable.ic_flag_russia, 7)),
        createCountryPair(CountryVO("CZ", R.string.czech_republic, R.drawable.ic_flag_czech_republic, 420)),
        createCountryPair(CountryVO("DE", R.string.germany, R.drawable.ic_flag_germany, 49)),
        createCountryPair(CountryVO("HU", R.string.hungary, R.drawable.ic_flag_hungary, 36)),
        createCountryPair(CountryVO("IS", R.string.iceland, R.drawable.ic_flag_iceland, 354)),
        createCountryPair(CountryVO("IT", R.string.italy, R.drawable.ic_flag_italy, 39)),
        createCountryPair(CountryVO("LV", R.string.latvia, R.drawable.ic_flag_latvia, 371)),
        createCountryPair(CountryVO("LT", R.string.lithuania, R.drawable.ic_flag_lithuania, 370)),
        createCountryPair(CountryVO("NL", R.string.neatherlands, R.drawable.ic_flag_neatherlands, 31)),
        createCountryPair(CountryVO("PL", R.string.poland, R.drawable.ic_flag_poland, 48)),
        createCountryPair(CountryVO("SE", R.string.sweden, R.drawable.ic_flag_sweden, 46)),
        createCountryPair(CountryVO("UA", R.string.ukraine, R.drawable.ic_flag_ukraine, 380))
)

val DEFAULT_COUNTRY = COUNTRIES["RU"]!!

/**
 * Получает страну пользователя на основе настроек его устройства.
 * Если его страна не поддерживается, то выдает дефолтную страну.
 *
 * @return Пара (ViewObject страны)-(Совпадает ли она с настройками пользователя)
 */
fun getDefaultCountryVO(): Pair<CountryVO, Boolean> {
    val regionCode = Locale.getDefault().country
    val country = COUNTRIES[regionCode]

    return if (country != null) {
        Pair(country, true)
    } else {
        Pair(DEFAULT_COUNTRY, false)
    }
}

/**
 * Форматирует номер телефона в интернациональный формат
 *
 * @param phoneNumber Номер телефона, который нужно отформатировать
 */
fun PhoneNumberUtil.formatPhoneNumber(phoneNumber: String): String {
    val number = if (!phoneNumber.startsWith("+")) {
        "+$phoneNumber"
    } else {
        phoneNumber
    }

    return format(parse(number, null), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
}

/**
 * Проверяет валидность номера телефона.
 *
 * @param phone Номер телефона
 * @return True если номер телефона валиден, False - если не валиден.
 */
fun PhoneNumberUtil.isPhoneNumberValid(phone: String): Boolean {
    var newPhone = phone

    if (!newPhone.startsWith("+")) {
        newPhone = "+$phone"
    }

    return try {
        isValidNumber(parse(newPhone, null))
    } catch (e: NumberParseException) {
        false
    }
}

private fun createCountryPair(countryVO: CountryVO): Pair<String, CountryVO> {
    return countryVO.regionCode to countryVO
}