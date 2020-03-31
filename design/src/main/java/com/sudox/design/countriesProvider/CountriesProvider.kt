package com.sudox.design.countriesProvider

import android.content.Context
import com.sudox.design.R
import com.sudox.design.countriesProvider.entries.Country

val COUNTRIES = hashMapOf(
        createSupportedCountryPair(Country("RU", R.string.russia, R.drawable.ic_flag_russia, 7)),
        createSupportedCountryPair(Country("CZ", R.string.czech_republic, R.drawable.ic_flag_czech_republic, 420)),
        createSupportedCountryPair(Country("DE", R.string.germany, R.drawable.ic_flag_germany, 49)),
        createSupportedCountryPair(Country("HU", R.string.hungary, R.drawable.ic_flag_hungary, 36)),
        createSupportedCountryPair(Country("IS", R.string.iceland, R.drawable.ic_flag_iceland, 354)),
        createSupportedCountryPair(Country("IT", R.string.italy, R.drawable.ic_flag_italy, 39)),
        createSupportedCountryPair(Country("LV", R.string.latvia, R.drawable.ic_flag_latvia, 371)),
        createSupportedCountryPair(Country("LT", R.string.lithuania, R.drawable.ic_flag_lithuania, 370)),
        createSupportedCountryPair(Country("NL", R.string.neatherlands, R.drawable.ic_flag_neatherlands, 31)),
        createSupportedCountryPair(Country("PL", R.string.poland, R.drawable.ic_flag_poland, 48)),
        createSupportedCountryPair(Country("SE", R.string.sweden, R.drawable.ic_flag_sweden, 46)),
        createSupportedCountryPair(Country("UA", R.string.ukraine, R.drawable.ic_flag_ukraine, 380))
)

/**
 * Возвращает отсортированный по имени список стран.
 *
 * @param context Контекст приложения/активности
 * @return Отсортированный список стран
 */
fun getCountries(context: Context): List<Country> {
    return COUNTRIES.values.sortedBy {
        context.getString(it.nameTextId)
    }
}

private fun createSupportedCountryPair(country: Country): Pair<String, Country> {
    return country.regionCode to country
}