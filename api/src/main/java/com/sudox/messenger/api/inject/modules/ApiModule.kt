package com.sudox.messenger.api.inject.modules

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.ApiImpl
import com.sudox.messenger.api.inject.ApiScope
import dagger.Module
import dagger.Provides

@Module
open class ApiModule {

    @ApiScope
    @Provides
    open fun provideApi(): Api {
        return ApiImpl()
    }
}