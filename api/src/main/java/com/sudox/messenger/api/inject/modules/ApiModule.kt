package com.sudox.messenger.api.inject.modules

import com.sudox.messenger.api.ApiCore
import com.sudox.messenger.api.ApiCoreImpl
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.auth.AuthApiImpl
import com.sudox.messenger.api.inject.ApiScope
import dagger.Module
import dagger.Provides

@Module
open class ApiModule {

    @Provides
    @ApiScope
    open fun provideApiCore(): ApiCore {
        return ApiCoreImpl()
    }

    @Provides
    @ApiScope
    open fun provideAuthApi(): AuthApi {
        return AuthApiImpl()
    }
}