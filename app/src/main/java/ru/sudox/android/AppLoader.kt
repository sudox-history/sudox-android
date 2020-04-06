package ru.sudox.android

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import ru.sudox.api.connections.impl.WebSocketConnection
import ru.sudox.api.inject.ApiModule
import ru.sudox.android.countries.inject.CountriesModule
import ru.sudox.android.inject.DaggerLoaderComponent
import ru.sudox.android.inject.LoaderComponent
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import ru.sudox.android.core.inject.CoreLoaderModule

class AppLoader : Application() {

    private var connector: AppConnector? = null

    companion object {
        var loaderComponent: LoaderComponent? = null
    }

    override fun onCreate() {
        super.onCreate()

        loaderComponent = DaggerLoaderComponent
                .builder()
                .coreLoaderModule(CoreLoaderModule(this))
                .countriesModule(CountriesModule(PhoneNumberUtil.createInstance(this)))
                .apiModule(ApiModule(WebSocketConnection(), ObjectMapper()
                        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                        .registerKotlinModule()
                )).build()

        loaderComponent!!.inject(this)

        connector = AppConnector()
        connector!!.start()
    }

    override fun onTerminate() {
        connector!!.destroy()
        super.onTerminate()
    }
}