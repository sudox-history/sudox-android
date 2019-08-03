package com.sudox.messenger.api.inject

import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.core.ApiCore
import com.sudox.messenger.api.inject.modules.ApiModule
import dagger.Component

@ApiScope
@Component(modules = [
    ApiModule::class
])
interface ApiComponent {
    fun authApi(): AuthApi
    fun apiCore(): ApiCore
}