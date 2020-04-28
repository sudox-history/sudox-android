package ru.sudox.android

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import ru.sudox.api.connections.impl.WebSocketConnection
import ru.sudox.api.inject.ApiModule
import ru.sudox.android.countries.inject.CountriesModule
import ru.sudox.android.inject.components.LoaderComponent
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.msgpack.jackson.dataformat.MessagePackFactory
import ru.sudox.android.core.inject.CoreLoaderModule
import ru.sudox.android.inject.DatabaseModule
import ru.sudox.android.inject.components.DaggerLoaderComponent

/**
 * Основной класс приложения.
 *
 * Отвечает за:
 * 1) Создание корневого компонента DI
 * 2) Инициализацию коннектора API
 */
class AppLoader : Application() {

    private var connector: AppConnector? = null

    companion object {
        var loaderComponent: LoaderComponent? = null
    }

    override fun onCreate() {
        super.onCreate()

        val phoneNumberUtil = PhoneNumberUtil.createInstance(this)
        val objectMapper = ObjectMapper(MessagePackFactory())
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .registerKotlinModule()

        loaderComponent = DaggerLoaderComponent
                .builder()
                .databaseModule(DatabaseModule("sudox"))
                .coreLoaderModule(CoreLoaderModule(this))
                .countriesModule(CountriesModule(phoneNumberUtil))
                .apiModule(ApiModule(WebSocketConnection(), objectMapper, phoneNumberUtil))
                .build()

        loaderComponent!!.inject(this)
        connector = AppConnector()
        connector!!.start()
    }

    override fun onTerminate() {
        connector!!.destroy()
        super.onTerminate()
    }
}