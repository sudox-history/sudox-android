package com.sudox.messenger.android.inject

import com.sudox.messenger.android.core.inject.CoreComponent
import com.sudox.messenger.android.core.inject.CoreModule
import com.sudox.messenger.android.countries.inject.CountriesComponent
import com.sudox.messenger.android.countries.inject.CountriesModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    CoreModule::class,
    CountriesModule::class
])
interface AppComponent : CoreComponent, CountriesComponent