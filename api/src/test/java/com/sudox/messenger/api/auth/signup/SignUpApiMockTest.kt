package com.sudox.messenger.api.auth.signup

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.auth.signin.EXCHANGE_ACCEPTED_PHONE_CODE_MIN
import com.sudox.messenger.api.common.ApiError
import com.sudox.messenger.api.common.ApiResult
import com.sudox.messenger.api.inject.DaggerApiComponent
import com.sudox.messenger.api.inject.modules.mocks.ApiModuleMock
import com.sudox.messenger.api.inject.modules.mocks.AuthApiModuleMock
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SignUpApiMockTest : Assert() {

    private lateinit var api: Api
    private lateinit var authApi: AuthApi
    private lateinit var signUpApi: SignUpApi

    @Before
    fun setUp() {
        val component = DaggerApiComponent
                .builder()
                .apiModule(ApiModuleMock())
                .authApiModule(AuthApiModuleMock())
                .build()

        api = component.api()
        authApi = component.authApi()
        signUpApi = component.signUpApi()
    }

    @Test
    fun testConfirmingPhoneWhenNotConnected() {
        val result = signUpApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN) as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.NOT_CONNECTED, result!!.errorCode)
    }

    @Test
    fun testConfirmingPhoneWhenAuthNotStarted() {
        api.startConnection()

        val result = signUpApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN) as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.INVALID_FORMAT, result!!.errorCode)
    }

    @Test
    fun testConfirmingPhoneWhenItRegistered() {
        api.startConnection()
        authApi.start("79111111111")

        val result = signUpApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN) as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.INVALID_PHONE, result!!.errorCode)
    }

    @Test
    fun testConfirmingPhoneWhenCodeInvalid() {
        api.startConnection()
        authApi.start("79000000000")

        val result = signUpApi.confirmPhone(1) as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.INVALID_CODE, result!!.errorCode)
    }

    @Test
    fun testWhenAllValid() {
        api.startConnection()
        authApi.start("79000000000")

        assertNotNull(signUpApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN) as? ApiResult<Nothing>)
    }
}