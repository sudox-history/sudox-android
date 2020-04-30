package ru.sudox.android.inject.components

import ru.sudox.api.inject.ApiModule
import ru.sudox.android.AppLoader
import ru.sudox.android.auth.inject.AuthComponent
import ru.sudox.android.core.inject.CoreActivityModule
import ru.sudox.android.core.inject.CoreLoaderComponent
import ru.sudox.android.countries.inject.CountriesComponent
import ru.sudox.android.countries.inject.CountriesModule
import dagger.Component
import ru.sudox.android.AppConnector
import ru.sudox.android.account.inject.AccountComponent
import ru.sudox.android.account.inject.AccountModule
import ru.sudox.android.auth.inject.AuthModule
import ru.sudox.android.core.inject.CoreLoaderModule
import ru.sudox.android.inject.DatabaseModule
import ru.sudox.android.inject.ViewModelModule
import ru.sudox.android.managers.vos.MainAppBarVO
import ru.sudox.api.common.SudoxApi
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApiModule::class,
    AccountModule::class,
    DatabaseModule::class,
    CoreLoaderModule::class,
    CountriesModule::class,
    ViewModelModule::class,
    AuthModule::class
])
interface LoaderComponent : CoreLoaderComponent, CountriesComponent, AuthComponent, AccountComponent {
    fun activityComponent(coreActivityModule: CoreActivityModule): ActivityComponent
    fun inject(mainAppBarVO: MainAppBarVO)
    fun inject(appConnector: AppConnector)
    fun inject(appLoader: AppLoader)
    fun sudoxApi(): SudoxApi
}