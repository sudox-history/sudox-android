package com.sudox.messenger.api.auth.signup

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.auth.signin.ACCOUNT_KEY
import com.sudox.messenger.api.auth.signin.ACCOUNT_KEY_HASH
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

    @Test
    fun testFinishingWhenNotConnected() {
        val result = signUpApi.finish("TheMax", ACCOUNT_KEY_HASH) as? ApiResult.Failure

        assertNull(authApi.currentToken)
        assertNull(authApi.currentToken)
        assertEquals(ApiError.NOT_CONNECTED, result!!.errorCode)
        assertNotNull(result)
    }

    @Test
    fun testFinishingWhenAuthNotStarted() {
        api.startConnection()

        val result = signUpApi.finish("TheMax", ACCOUNT_KEY_HASH) as? ApiResult.Failure

        assertNull(authApi.currentToken)
        assertNull(authApi.currentToken)
        assertEquals(ApiError.INVALID_FORMAT, result!!.errorCode)
        assertNotNull(result)
    }

    @Test
    fun testFinishingWhenNicknameNotValid() {
        api.startConnection()
        authApi.start("79000000000")

        val result = signUpApi.finish("T", ACCOUNT_KEY_HASH) as? ApiResult.Failure

        assertNull(authApi.currentToken)
        assertNull(authApi.currentToken)
        assertEquals(ApiError.INVALID_FORMAT, result!!.errorCode)
        assertNotNull(result)
    }

    @Test
    fun testFinishingWhenPhoneNotConfirmed() {
        api.startConnection()
        authApi.start("79000000000")

        val result = signUpApi.finish("TheMax", ACCOUNT_KEY_HASH) as? ApiResult.Failure

        assertNull(authApi.currentToken)
        assertNull(authApi.currentToken)
        assertEquals(ApiError.INVALID_PHONE, result!!.errorCode)
        assertNotNull(result)
    }

    @Test
    fun testFinishingWhenKeyHashNotValid() {
        api.startConnection()
        authApi.start("79000000000")
        signUpApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN)

        val result = signUpApi.finish("TheMax", ByteArray(0)) as? ApiResult.Failure

        assertNull(authApi.currentToken)
        assertNull(authApi.currentToken)
        assertEquals(ApiError.INVALID_KEY, result!!.errorCode)
        assertNotNull(result)
    }

    @Test
    fun testFinishingWhenAllValid() {
        api.startConnection()
        authApi.start("79000000000")
        signUpApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN)

        val result = signUpApi.finish("TheMax", ACCOUNT_KEY_HASH) as? ApiResult.Success

        assertNotNull(authApi.currentToken)
        assertEquals(authApi.currentToken, result!!.data)
        assertEquals(authApi.currentKey, ACCOUNT_KEY)
    }
}