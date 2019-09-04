package com.sudox.messenger.api.auth.signin

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.common.ApiError
import com.sudox.messenger.api.common.ApiResult
import com.sudox.messenger.api.inject.DaggerApiComponent
import com.sudox.messenger.api.inject.modules.mocks.ApiModuleMock
import com.sudox.messenger.api.inject.modules.mocks.AuthApiModuleMock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

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
    fun testPhoneConfirmingWhenNotConnected() {
        val result = signInApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN) as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.NOT_CONNECTED, result!!.errorCode)
    }

    @Test
    fun testPhoneConfirmingWhenAuthNotStarted() {
        api.startConnection()

        val result = signInApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN) as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.INVALID_FORMAT, result!!.errorCode)
    }

    @Test
    fun testPhoneConfirmingWhenItNotRegistered() {
        api.startConnection()
        authApi.start("79000000000")

        val result = signInApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN) as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.INVALID_PHONE, result!!.errorCode)
    }

    @Test
    fun testPhoneConfirmingWhenItAlreadyConfirmed() {
        api.startConnection()
        authApi.start("79111111111")
        signInApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN)

        val result = signInApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN) as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.INVALID_PHONE, result!!.errorCode)
    }

    @Test
    fun testPhoneConfirmingWhenCodeInvalid() {
        api.startConnection()
        authApi.start("79111111111")

        val result = signInApi.confirmPhone(1) as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.INVALID_CODE, result!!.errorCode)
    }

    @Test
    fun testWhenExchangeAccepted() {
        api.startConnection()
        authApi.start("79111111111")

        val semaphore = Semaphore(0)
        var eventData: Any? = null

        authApi.eventEmitter.on(EXCHANGE_ACCEPTED_EVENT_NAME) {
            eventData = it!!
            semaphore.release()
        }

        signInApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN)

        semaphore.tryAcquire(5, TimeUnit.SECONDS)
        assertEquals(ACCOUNT_KEY, eventData)
    }

    @Test
    fun testWhenExchangeDenied() {
        api.startConnection()
        authApi.start("79111111111")

        val semaphore = Semaphore(0)
        var eventEmitted = false

        authApi.eventEmitter.on(EXCHANGE_DENIED_EVENT_NAME) {
            eventEmitted = true
            semaphore.release()
        }

        signInApi.confirmPhone(EXCHANGE_DENIED_PHONE_CODE_MIN)

        semaphore.tryAcquire(5, TimeUnit.SECONDS)
        assertTrue(eventEmitted)
    }

    @Test
    fun testWhenConnectionDroppedDuringKeyExchange() {
        api.startConnection()
        authApi.start("79111111111")

        val semaphore = Semaphore(0)
        var eventEmitted = false

        authApi.eventEmitter.on(EXCHANGE_DROPPED_EVENT_NAME) {
            eventEmitted = true
            semaphore.release()
        }

        signInApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN)
        api.endConnection()

        semaphore.tryAcquire(5, TimeUnit.SECONDS)
        assertTrue(eventEmitted)
    }

    @Test
    fun testWhenStartedSessionWithNewPhone() {
        api.startConnection()
        authApi.start("79111111111")

        val semaphore = Semaphore(0)
        var eventEmitted = false

        authApi.eventEmitter.on(EXCHANGE_ACCEPTED_EVENT_NAME) {
            eventEmitted = true
            semaphore.release()
        }

        signInApi.confirmPhone(EXCHANGE_ACCEPTED_PHONE_CODE_MIN)
        authApi.start("79111111112")

        semaphore.tryAcquire(5, TimeUnit.SECONDS)
        assertFalse(eventEmitted)
    }
}