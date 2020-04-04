package com.sudox.messenger.android

import android.app.Application
import com.sudox.api.SudoxApi
import com.sudox.api.connections.impl.WebSocketConnection
import com.sudox.api.inject.ApiModule
import com.sudox.api.serializers.impl.JSONSerializer
import com.sudox.messenger.android.countries.inject.CountriesModule
import com.sudox.messenger.android.inject.DaggerLoaderComponent
import com.sudox.messenger.android.inject.LoaderComponent
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import javax.inject.Inject

class AppLoader : Application() {

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
                .apiModule(ApiModule(WebSocketConnection(), JSONSerializer()))
                .build()
    }
}