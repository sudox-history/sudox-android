package com.sudox.messenger.api.auth.signin

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.inject.DaggerApiComponent
import com.sudox.messenger.api.inject.modules.mocks.ApiModuleMock
import com.sudox.messenger.api.inject.modules.mocks.AuthApiModuleMock
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SignInApiMockTest : Assert() {

    private lateinit var api: Api
    private lateinit var authApi: AuthApi
    private lateinit var signInApi: SignInApi

    @Before
    fun setUp() {
        val component = DaggerApiComponent
                .builder()
                .apiModule(ApiModuleMock())
                .authApiModule(AuthApiModuleMock())
                .build()

        api = component.api()
        authApi = component.authApi()
        signInApi = component.signInApi()
    }

    @Test
    fun testConfirmPhoneWhenNotConnected() {
        
    }
}