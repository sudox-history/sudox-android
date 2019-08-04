package com.sudox.messenger.api.auth

import com.sudox.messenger.api.Api
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

class AuthApiMockTest : Assert() {

    private lateinit var api: Api
    private lateinit var authApi: AuthApi

    @Before
    fun setUp() {
        val component = DaggerApiComponent
                .builder()
                .apiModule(ApiModuleMock())
                .authApiModule(AuthApiModuleMock())
                .build()

        api = component.api()
        authApi = component.authApi()
    }

    @Test
    fun testStartingWhenNotConnected() {
        val result = authApi.start("79000000000") as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.NOT_CONNECTED, result!!.errorCode)
    }

    @Test
    fun testStartingWhenPhoneFormatInvalid() {
        api.startConnection()

        val result = authApi.start("1234567890") as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.INVALID_FORMAT, result!!.errorCode)
    }

    @Test
    fun testStartingWhenSessionWithPhoneAlreadyStarted() {
        api.startConnection()
        authApi.start("79000000000")

        val result = authApi.start("79000000000") as? ApiResult.Failure

        assertNotNull(result)
        assertEquals(ApiError.INVALID_PHONE, result!!.errorCode)
    }

    @Test
    fun testStartingWhenPhoneNotRegistered() {
        api.startConnection()

        var eventData: Array<*>? = null
        val semaphore = Semaphore(0)

        authApi.eventEmitter.on(AUTH_STARTED_EVENT_NAME) {
            eventData = it as? Array<*>
            semaphore.release()
        }

        val result = authApi.start("79000000000") as? ApiResult.Success

        assertNotNull(result)
        assertFalse(result!!.data!!)

        semaphore.tryAcquire(5, TimeUnit.SECONDS)
        assertNotNull(eventData)
        assertArrayEquals(arrayOf("79000000000", false), eventData)
    }

    @Test
    fun testStartingWhenPhoneRegistered() {
        api.startConnection()

        var eventData: Array<*>? = null
        val semaphore = Semaphore(0)

        authApi.eventEmitter.on(AUTH_STARTED_EVENT_NAME) {
            eventData = it as? Array<*>
            semaphore.release()
        }

        val result = authApi.start("79111111111") as? ApiResult.Success

        assertNotNull(result)
        assertTrue(result!!.data!!)

        semaphore.tryAcquire(5, TimeUnit.SECONDS)
        assertNotNull(eventData)
        assertArrayEquals(arrayOf("79111111111", true), eventData)
    }
}