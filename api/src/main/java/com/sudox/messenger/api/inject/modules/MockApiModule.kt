package com.sudox.messenger.api.inject.modules

import com.sudox.messenger.api.core.ApiCore
import com.sudox.messenger.api.core.ApiCoreMock
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.auth.AuthApiMock

class MockApiModule : ApiModule() {

    override fun provideApiCore(): ApiCore {
        return ApiCoreMock()
    }

    override fun provideAuthApi(): AuthApi {
        return AuthApiMock()
    }
}