package com.sudox.messenger.api.inject.modules

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.auth.AuthApiImpl
import com.sudox.messenger.api.auth.signin.SignInApi
import com.sudox.messenger.api.auth.signin.SignInApiImpl
import com.sudox.messenger.api.inject.ApiScope
import dagger.Module
import dagger.Provides

@Module
open class AuthApiModule {

    @ApiScope
    @Provides
    open fun provideAuthApi(api: Api): AuthApi {
        return AuthApiImpl()
    }

    @ApiScope
    @Provides
    open fun provideSignInApi(api: Api, authApi: AuthApi): SignInApi {
        return SignInApiImpl(api, authApi)
    }
}