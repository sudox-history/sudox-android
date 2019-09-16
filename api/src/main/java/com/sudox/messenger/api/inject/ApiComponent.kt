package com.sudox.messenger.api.inject

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.auth.signin.SignInApi
import com.sudox.messenger.api.auth.signup.SignUpApi
import com.sudox.messenger.api.inject.modules.ApiModule
import com.sudox.messenger.api.inject.modules.AuthApiModule
import dagger.Component

@ApiScope
@Component(modules = [
    ApiModule::class,
    AuthApiModule::class
])
interface ApiComponent {
    fun api(): Api
    fun authApi(): AuthApi
    fun signInApi(): SignInApi
    fun signUpApi(): SignUpApi
}