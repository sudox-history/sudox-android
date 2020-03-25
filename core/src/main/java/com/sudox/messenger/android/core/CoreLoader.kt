package com.sudox.messenger.android.core

import com.sudox.design.countriesProvider.CountriesProvider

interface CoreLoader {
    @Deprecated(message = "Кэш будет убран в будущем!")
    fun getCountriesProvider(): CountriesProvider
}