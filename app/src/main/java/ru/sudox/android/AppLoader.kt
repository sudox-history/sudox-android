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
import io.realm.Realm
import ru.sudox.android.core.inject.CoreLoaderModule
import ru.sudox.android.inject.modules.DatabaseModule
import javax.inject.Inject

/**
 * Основной класс приложения.
 *
 * Отвечает за:
 * 1) Создание корневого компонента DI
 * 2) Инициализацию коннектора API
 */
class AppLoader : Application() {

    @Inject
    @JvmField
    var encryptor: AppEncryptor? = null

    @Inject
    @JvmField
    var connector: AppConnector? = null

    companion object {
        var loaderComponent: LoaderComponent? = null
    }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        loaderComponent = DaggerLoaderComponent
                .builder()
                .databaseModule(DatabaseModule("sudox", 1L))
                .coreLoaderModule(CoreLoaderModule(this))
                .countriesModule(CountriesModule(PhoneNumberUtil.createInstance(this)))
                .apiModule(ApiModule(WebSocketConnection(), ObjectMapper()
                        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                        .registerKotlinModule()
                )).build()

        loaderComponent!!.inject(this)
        connector!!.start()
    }

    override fun onTerminate() {
        connector!!.destroy()
        super.onTerminate()
    }
}