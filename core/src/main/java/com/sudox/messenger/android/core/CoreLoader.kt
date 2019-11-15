package com.sudox.messenger.android.core

import com.sudox.design.countriesProvider.CountriesProvider

interface CoreLoader {
    fun getCountriesProvider(): CountriesProvider
}