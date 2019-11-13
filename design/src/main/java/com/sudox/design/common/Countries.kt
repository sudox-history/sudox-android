package com.sudox.design.common

import com.sudox.design.R
import com.sudox.design.common.entries.Country

@SuppressWarnings("MagicNumber")
val supportedCountries = arrayOf(
        Country("RU", R.string.russia, R.drawable.ic_flag_undefined, 7),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("SS", R.string.uae, R.drawable.ic_flag_undefined, 971),
        Country("RU", R.string.russia, R.drawable.ic_flag_undefined, 7),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380),
        Country("UA", R.string.ukraine, R.drawable.ic_flag_undefined, 380)
)

fun findCountryByRegionCode(regionCode: String): Country? {
    return supportedCountries.find {
        it.regionCode == regionCode
    }
}