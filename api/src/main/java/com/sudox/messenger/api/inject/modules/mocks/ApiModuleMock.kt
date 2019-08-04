package com.sudox.messenger.api.inject.modules.mocks

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.ApiMock
import com.sudox.messenger.api.inject.modules.ApiModule

class ApiModuleMock : ApiModule() {

    override fun provideApi(): Api {
        return ApiMock()
    }
}