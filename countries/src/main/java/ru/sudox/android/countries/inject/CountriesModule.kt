package ru.sudox.android.countries.inject

import dagger.Module
import dagger.Provides
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import javax.inject.Singleton

@Module(includes = [CountriesUiModule::class])
class CountriesModule(
        private val phoneNumberUtil: PhoneNumberUtil
) {

    @Provides
    @Singleton
    fun providePhoneNumberUtil(): PhoneNumberUtil {
        return phoneNumberUtil
    }
}