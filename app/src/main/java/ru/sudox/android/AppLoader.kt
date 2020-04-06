package ru.sudox.android

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import ru.sudox.android.countries.inject.CountriesModule
import ru.sudox.android.inject.DaggerLoaderComponent
import ru.sudox.android.inject.LoaderComponent
import ru.sudox.android.managers.AppNetworkManager
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.connections.impl.WebSocketConnection
import ru.sudox.api.inject.ApiModule
import javax.inject.Inject

class AppLoader : Application() {

    private var networkManager: AppNetworkManager? = null

    companion object {
        var loaderComponent: LoaderComponent? = null
    }

    @Inject
    @JvmField
    var sudoxApi: SudoxApi? = null

    override fun onCreate() {
        super.onCreate()

        loaderComponent = DaggerLoaderComponent
                .builder()
                .countriesModule(CountriesModule(PhoneNumberUtil.createInstance(this)))
                .apiModule(ApiModule(WebSocketConnection(), ObjectMapper()
                        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                        .registerKotlinModule()
                )).build()

        loaderComponent!!.inject(this)

        networkManager = AppNetworkManager(sudoxApi!!)
        networkManager!!.register(this)
    }

    override fun onTerminate() {
        sudoxApi!!.endConnection()
        networkManager!!.unregister()
        super.onTerminate()
    }
}