package ru.sudox.android

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.rxjava3.disposables.Disposable
import ru.sudox.android.countries.inject.CountriesModule
import ru.sudox.android.inject.DaggerLoaderComponent
import ru.sudox.android.inject.LoaderComponent
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.common.SudoxApiStatus
import ru.sudox.api.connections.impl.WebSocketConnection
import ru.sudox.api.inject.ApiModule
import javax.inject.Inject

class AppLoader : Application() {

    companion object {
        var loaderComponent: LoaderComponent? = null
    }

    private var apiStatusDisposable: Disposable? = null

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

        sudoxApi!!.let {
            apiStatusDisposable = it.statusSubject.subscribe { status ->
                if (status == SudoxApiStatus.DISCONNECTED) {
                    it.startConnection()
                }
            }

            it.startConnection()
        }
    }

    override fun onTerminate() {
        apiStatusDisposable!!.dispose()
        sudoxApi!!.endConnection()
        super.onTerminate()
    }
}