package com.sudox.design.countriesProvider.entries

import android.content.Context

class Country(
        val regionCode: String,
        val nameTextId: Int,
        val flagImageId: Int,
        val countryCode: Int
) {

    fun getName(context: Context): String {
        return context.getString(nameTextId)
    }
}