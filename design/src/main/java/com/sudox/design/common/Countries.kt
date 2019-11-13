package com.sudox.design.common

import android.content.Context
import com.sudox.design.R
import com.sudox.design.common.entries.Country

@SuppressWarnings("MagicNumber")
val supportedCountries = arrayOf(
        Country("RU", R.string.russia, R.drawable.ic_flag_russia, 7),
        Country("CZ", R.string.czech_republic, R.drawable.ic_flag_czech_republic, 420),
        Country("DE", R.string.germany, R.drawable.ic_flag_germany, 49),
        Country("HU", R.string.hungary, R.drawable.ic_flag_hungary, 36),
        Country("IS", R.string.iceland, R.drawable.ic_flag_iceland, 354),
        Country("IT", R.string.italy, R.drawable.ic_flag_italy, 39),
        Country("LV", R.string.latvia, R.drawable.ic_flag_latvia, 371),
        Country("LT", R.string.lithuania, R.drawable.ic_flag_lithuania, 370),
        Country("NL", R.string.neatherlands, R.drawable.ic_flag_neatherlands, 31),
        Country("PL", R.string.poland, R.drawable.ic_flag_poland, 48),
        Country("SE", R.string.sweden, R.drawable.ic_flag_sweden, 46),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_ukraine, 380)
)

fun findCountryByRegionCode(regionCode: String): Country? {
    return supportedCountries.find {
        it.regionCode == regionCode
    }
}

fun sortCountriesByNames(context: Context): List<Country> {
    val countriesNames = HashMap<Country, String>(supportedCountries.size)

    supportedCountries.forEach {
        countriesNames[it] = it.getName(context)
    }

    return supportedCountries.sortedBy {
        countriesNames[it]
    }
}