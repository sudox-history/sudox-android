package com.sudox.messenger.api.inject.modules.mocks

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.auth.AuthApiMock
import com.sudox.messenger.api.auth.signin.SignInApi
import com.sudox.messenger.api.auth.signin.SignInApiMock
import com.sudox.messenger.api.auth.signup.SignUpApi
import com.sudox.messenger.api.auth.signup.SignUpApiMock
import com.sudox.messenger.api.inject.modules.AuthApiModule

class AuthApiModuleMock : AuthApiModule() {

    override fun provideAuthApi(api: Api): AuthApi {
        return AuthApiMock(api)
    }

    override fun provideSignInApi(api: Api, authApi: AuthApi): SignInApi {
        return SignInApiMock(api, authApi)
    }

    override fun provideSignUpApi(api: Api, authApi: AuthApi): SignUpApi {
        return SignUpApiMock(api, authApi)
    }
}