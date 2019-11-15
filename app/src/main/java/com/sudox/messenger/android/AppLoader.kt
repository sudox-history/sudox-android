package com.sudox.messenger.android

import android.app.Application
import android.content.res.Configuration
import com.sudox.design.countriesProvider.CountriesProvider
import com.sudox.design.getLocale
import com.sudox.design.loadDesignComponents
import com.sudox.messenger.android.core.CoreLoader

@Suppress("unused")
class AppLoader : Application(), CoreLoader {

    private var countriesProvider: CountriesProvider? = null

    override fun onCreate() {
        super.onCreate()

        loadDesignComponents(this)

        countriesProvider = CountriesProvider(this)
        countriesProvider!!.tryLoadOrSort()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.getLocale() != applicationContext.resources.configuration.getLocale()) {
            countriesProvider!!.sortAndCache()
        }

        super.onConfigurationChanged(newConfig)
    }

    override fun getCountriesProvider(): CountriesProvider {
        return countriesProvider!!
    }
}