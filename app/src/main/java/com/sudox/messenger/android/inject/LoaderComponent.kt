package com.sudox.messenger.android.inject

import com.sudox.api.inject.ApiModule
import com.sudox.messenger.android.AppLoader
import com.sudox.messenger.android.auth.inject.AuthComponent
import com.sudox.messenger.android.core.inject.CoreActivityComponent
import com.sudox.messenger.android.core.inject.CoreActivityModule
import com.sudox.messenger.android.core.inject.CoreLoaderComponent
import com.sudox.messenger.android.countries.inject.CountriesComponent
import com.sudox.messenger.android.countries.inject.CountriesModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, CountriesModule::class])
interface LoaderComponent : CoreLoaderComponent, CountriesComponent, AuthComponent {
    fun activityComponent(coreActivityModule: CoreActivityModule): ActivityComponent
    fun inject(appLoader: AppLoader)
}