package ru.sudox.android.injector

import android.app.Application
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ru.sudox.android.countries.api.CountriesFeatureApi
import ru.sudox.android.countries.impl.CountriesFeatureImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object CountriesFeatureModule {

    @Provides
    @Singleton
    fun providesApi(app: Application, phoneNumberUtil: PhoneNumberUtil): CountriesFeatureApi =
        CountriesFeatureImpl(app.resources, app.assets, phoneNumberUtil)
}