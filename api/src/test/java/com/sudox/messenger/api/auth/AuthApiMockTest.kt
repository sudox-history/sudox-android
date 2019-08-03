package com.sudox.messenger.api.auth

import com.sudox.messenger.api.ApiError
import com.sudox.messenger.api.ApiResult
import com.sudox.messenger.api.core.ApiCore
import com.sudox.messenger.api.inject.DaggerApiComponent
import com.sudox.messenger.api.inject.modules.MockApiModule
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class AuthApiMockTest : Assert() {

    private var authApi: AuthApi? = null
    private var apiCore: ApiCore? = null

    @Before
    fun setUp() {
        val component = DaggerApiComponent
                .builder()
                .apiModule(MockApiModule())
                .build()

        authApi = component.authApi()
        apiCore = component.apiCore()
    }

    @Test
    fun testStartWhenPhoneInvalid() {
        apiCore!!.start()

        val result = authApi!!.start("1234567890")

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testStartWhenPhoneNotRegistered() {
        apiCore!!.start()

        val result = authApi!!.start("79111111111")

        assertTrue(result is ApiResult.Success)
        assertFalse((result as ApiResult.Success).data!!)
    }

    @Test
    fun testStartWhenPhoneRegistered() {
        apiCore!!.start()

        val result = authApi!!.start(PHONE_REGISTERED)

        assertTrue(result is ApiResult.Success)
        assertTrue((result as ApiResult.Success).data!!)
    }

    @Test
    fun testStartWhenConnectionNotInstalled() {
        val result = authApi!!.start("79111111111") as ApiResult.Failure
        assertEquals(ApiError.NOT_CONNECTED, result.errorCode)
    }

    @Test
    fun testSignInConfirmPhoneWhenPhoneInvalid() {
        apiCore!!.start()

        val publicKey = ByteArray(PUBLIC_KEY_LENGTH)
        val result = authApi!!.confirmPhone("1234567890", PHONE_CODE_WHEN_EXCHANGE_ACCEPTED, publicKey)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignInConfirmPhoneWhenCodeInvalid() {
        apiCore!!.start()

        val publicKey = ByteArray(PUBLIC_KEY_LENGTH)
        val result = authApi!!.confirmPhone(PHONE_REGISTERED, "15793", publicKey)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_CODE, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignInConfirmPhoneWhenExchangeAccepted() {
        apiCore!!.start()

        val semaphore = Semaphore(0)
        var eventData: Any? = null

        authApi!!.authEventEmitter.once(AUTH_EXCHANGE_ACCEPTED_EVENT) {
            eventData = it
            semaphore.acquire()
        }

        val result = authApi!!.confirmPhone(PHONE_REGISTERED,
                PHONE_CODE_WHEN_EXCHANGE_ACCEPTED,
                ByteArray(PUBLIC_KEY_LENGTH))

        semaphore.tryAcquire(5, TimeUnit.SECONDS)

        assertTrue(result is ApiResult.Success)
        assertEquals(EXCHANGE_CODE, (result as ApiResult.Success).data)
        assertArrayEquals(arrayOf(ENCRYPTED_ACCOUNT_KEY, RECIPIENT_PUBLIC_KEY), eventData as Array<*>)
    }

    @Test
    fun testSignInConfirmPhoneWhenExchangeDenied() {
        apiCore!!.start()

        val semaphore = Semaphore(0)
        var eventCalled = false

        authApi!!.authEventEmitter.once(AUTH_EXCHANGE_DENIED_EVENT) {
            eventCalled = true
            semaphore.acquire()
        }

        val result = authApi!!.confirmPhone(PHONE_REGISTERED,
                PHONE_CODE_WHEN_EXCHANGE_DENIED,
                ByteArray(PUBLIC_KEY_LENGTH))

        semaphore.tryAcquire(5, TimeUnit.SECONDS)

        assertTrue(result is ApiResult.Success)
        assertEquals(EXCHANGE_CODE, (result as ApiResult.Success).data)
        assertTrue(eventCalled)
    }

    @Test
    fun testSignInConfirmPhoneWhenConnectionNotInstalled() {
        val result = authApi!!.confirmPhone(PHONE_REGISTERED,
                PHONE_CODE_WHEN_EXCHANGE_DENIED,
                ByteArray(PUBLIC_KEY_LENGTH))

        assertEquals(ApiError.NOT_CONNECTED, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignUpConfirmPhoneWhenPhoneInvalid() {
        apiCore!!.start()

        val result = authApi!!.confirmPhone("1234567890", PHONE_CODE_WHEN_EXCHANGE_ACCEPTED)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignUpConfirmPhoneWhenCodeInvalid() {
        apiCore!!.start()

        val result = authApi!!.confirmPhone(PHONE_REGISTERED, "15793")

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_CODE, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignUpConfirmPhoneWhenConnectionNotInstalled() {
        val result = authApi!!.confirmPhone(PHONE_REGISTERED, PHONE_CODE_WHEN_EXCHANGE_DENIED)
        assertEquals(ApiError.NOT_CONNECTED, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSuccessSignUpConfirmPhone() {
        apiCore!!.start()

        val result = authApi!!.confirmPhone(PHONE_REGISTERED, PHONE_CODE_WHEN_EXCHANGE_ACCEPTED)

        assertTrue(result is ApiResult.Success)
    }

    @Test
    fun testSignUpFinishWhenPhoneInvalid() {
        apiCore!!.start()

        val result = authApi!!.finish("1234567890", "themax", ACCOUNT_KEY_HASH)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignUpFinishWhenNicknameInvalid() {
        apiCore!!.start()

        val result = authApi!!.finish("79000000000", "@", ACCOUNT_KEY_HASH)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignUpFinishWhenHashInvalid() {
        apiCore!!.start()

        val result = authApi!!.finish("79000000000", "themax", ByteArray(0))

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_KEY, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignUpFinishWhenConnectionNotInstalled() {
        val result = authApi!!.finish(PHONE_REGISTERED, "themax", ACCOUNT_KEY_HASH)
        assertEquals(ApiError.NOT_CONNECTED, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSuccessSignUpFinish() {
        apiCore!!.start()

        val result = authApi!!.finish(PHONE_REGISTERED, "themax", ACCOUNT_KEY_HASH)

        assertTrue(result is ApiResult.Success)
        assertEquals(TOKEN, (result as ApiResult.Success).data)
    }

    @Test
    fun testSignInFinishWhenPhoneInvalid() {
        apiCore!!.start()

        val result = authApi!!.finish("1234567890", ACCOUNT_KEY_HASH)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignInFinishWhenHashInvalid() {
        apiCore!!.start()

        val result = authApi!!.finish(PHONE_REGISTERED, ByteArray(0))

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_KEY, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignInFinishWhenConnectionNotInstalled() {
        val result = authApi!!.finish(PHONE_REGISTERED, ACCOUNT_KEY_HASH)
        assertEquals(ApiError.NOT_CONNECTED, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSuccessSignInFinish() {
        apiCore!!.start()

        val result = authApi!!.finish(PHONE_REGISTERED, ACCOUNT_KEY_HASH)

        assertTrue(result is ApiResult.Success)
        assertEquals(TOKEN, (result as ApiResult.Success).data)
    }
}