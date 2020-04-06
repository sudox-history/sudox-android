package ru.sudox.android.countries.inject

import dagger.Module
import dagger.Provides
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import javax.inject.Singleton

@Module
class CountriesModule(phoneNumberUtil: PhoneNumberUtil) {

    val phoneNumberUtil: PhoneNumberUtil = phoneNumberUtil
        @Provides
        @Singleton
        get
}