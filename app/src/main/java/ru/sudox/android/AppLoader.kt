package ru.sudox.android

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.sudox.api.connections.impl.WebSocketConnection
import ru.sudox.api.inject.ApiModule
import ru.sudox.android.countries.inject.CountriesModule
import ru.sudox.android.inject.components.LoaderComponent
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.msgpack.jackson.dataformat.MessagePackFactory
import ru.sudox.android.account.inject.AccountModule
import ru.sudox.android.core.CoreLoader
import ru.sudox.android.core.inject.CoreLoaderComponent
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
class AppLoader : Application(), CoreLoader {

    private var connector: AppConnector? = null

    companion object {
        var loaderComponent: LoaderComponent? = null
    }

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

        val phoneNumberUtil = PhoneNumberUtil.createInstance(this)
        val objectMapper = ObjectMapper(MessagePackFactory())
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .registerKotlinModule()

        loaderComponent = DaggerLoaderComponent
                .builder()
                .accountModule(AccountModule(AppActivity::class.java, objectMapper, getString(R.string.account_type)))
                .databaseModule(DatabaseModule("sudox"))
                .coreLoaderModule(CoreLoaderModule(this))
                .countriesModule(CountriesModule(phoneNumberUtil))
                .apiModule(ApiModule(WebSocketConnection(), objectMapper))
                .build()

        loaderComponent!!.inject(this)
        connector = AppConnector()
        connector!!.start()
    }

    override fun getComponent(): CoreLoaderComponent {
        return loaderComponent as CoreLoaderComponent
    }

    override fun onTerminate() {
        connector!!.destroy()
        super.onTerminate()
    }
}