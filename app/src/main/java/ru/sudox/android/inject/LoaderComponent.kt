package ru.sudox.android.inject

import ru.sudox.api.inject.ApiModule
import ru.sudox.android.AppLoader
import ru.sudox.android.auth.inject.AuthComponent
import ru.sudox.android.core.inject.CoreActivityModule
import ru.sudox.android.core.inject.CoreLoaderComponent
import ru.sudox.android.countries.inject.CountriesComponent
import ru.sudox.android.countries.inject.CountriesModule
import dagger.Component
import ru.sudox.android.vos.ConnectAppBarVO
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, CountriesModule::class])
interface LoaderComponent : CoreLoaderComponent, CountriesComponent, AuthComponent {
    fun activityComponent(coreActivityModule: CoreActivityModule): ActivityComponent
    fun inject(connectAppBarVO: ConnectAppBarVO)
    fun inject(appLoader: AppLoader)
}