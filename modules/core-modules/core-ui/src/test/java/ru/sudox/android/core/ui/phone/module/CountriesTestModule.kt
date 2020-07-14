package ru.sudox.android.core.ui.phone.module

import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import org.mockito.Mockito
import ru.sudox.android.countries.api.CountriesFeatureApi
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class CountriesTestModule {

    @Singleton
    @Provides
    fun providePhoneNumber(): PhoneNumberUtil = PhoneNumberUtil.getInstance()

    @Singleton
    @Provides
    fun provideCountriesFeatureApi(): CountriesFeatureApi = Mockito.mock(CountriesFeatureApi::class.java)
}