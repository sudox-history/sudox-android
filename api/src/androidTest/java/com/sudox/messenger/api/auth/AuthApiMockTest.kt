package com.sudox.messenger.api.auth

import com.sudox.messenger.api.ApiError
import com.sudox.messenger.api.ApiResult
import com.sudox.messenger.api.inject.DaggerApiComponent
import com.sudox.messenger.api.inject.modules.MockApiModule
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class AuthApiMockTest : Assert() {

    private var authApi: AuthApi? = null

    @Before
    fun setUp() {
        val component = DaggerApiComponent
                .builder()
                .apiModule(MockApiModule())
                .build()

        authApi = component.authApi()
    }

    @Test
    fun testStartWhenPhoneInvalid() {
        val result = authApi!!.start("1234567890")

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testStartWhenPhoneNotRegistered() {
        val result = authApi!!.start("79111111111")

        assertTrue(result is ApiResult.Success)
        assertFalse((result as ApiResult.Success).data!!)
    }

    @Test
    fun testStartWhenPhoneRegistered() {
        val result = authApi!!.start(PHONE_REGISTERED)

        assertTrue(result is ApiResult.Success)
        assertTrue((result as ApiResult.Success).data!!)
    }

    @Test
    fun testSignInConfirmPhoneWhenPhoneInvalid() {
        val publicKey = ByteArray(PUBLIC_KEY_LENGTH)
        val result = authApi!!.confirmPhone("1234567890", PHONE_CODE_WHEN_EXCHANGE_ACCEPTED, publicKey)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignInConfirmPhoneWhenPublicKeyInvalid() {
        val publicKey = ByteArray(PUBLIC_KEY_LENGTH - 1)
        val result = authApi!!.confirmPhone(PHONE_REGISTERED, PHONE_CODE_WHEN_EXCHANGE_ACCEPTED, publicKey)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignInConfirmPhoneWhenCodeInvalid() {
        val publicKey = ByteArray(PUBLIC_KEY_LENGTH)
        val result = authApi!!.confirmPhone(PHONE_REGISTERED, "15793", publicKey)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_CODE, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignInConfirmPhoneWhenExchangeAccepted() {
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
        assertEquals(ACCOUNT_KEY, eventData)
    }

    @Test
    fun testSignInConfirmPhoneWhenExchangeDenied() {
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
    fun testSignUpConfirmPhoneWhenPhoneInvalid() {
        val result = authApi!!.confirmPhone("1234567890", PHONE_CODE_WHEN_EXCHANGE_ACCEPTED)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignUpConfirmPhoneWhenCodeInvalid() {
        val result = authApi!!.confirmPhone(PHONE_REGISTERED, "15793")

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_CODE, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignUpConfirmPhoneWhenResultIsSuccess() {
        val result = authApi!!.confirmPhone(PHONE_REGISTERED, PHONE_CODE_WHEN_EXCHANGE_ACCEPTED)

        assertTrue(result is ApiResult.Success)
    }

    @Test
    fun testSignUpFinishWhenPhoneInvalid() {
        val result = authApi!!.finish("1234567890", "themax", ACCOUNT_KEY_HASH)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignUpFinishWhenNicknameInvalid() {
        val result = authApi!!.finish("79000000000", "@", ACCOUNT_KEY_HASH)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignUpFinishWhenHashInvalid() {
        val result = authApi!!.finish("79000000000", "themax", ByteArray(0))

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_KEY, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSuccessSignUpFinish() {
        val result = authApi!!.finish("79000000000", "themax", ACCOUNT_KEY_HASH)

        assertTrue(result is ApiResult.Success)
        assertEquals(TOKEN, (result as ApiResult.Success).data)
    }

    @Test
    fun testSignInFinishWhenPhoneInvalid() {
        val result = authApi!!.finish("1234567890", ACCOUNT_KEY_HASH)

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_FORMAT, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSignInFinishWhenHashInvalid() {
        val result = authApi!!.finish("79000000000", ByteArray(0))

        assertTrue(result is ApiResult.Failure)
        assertEquals(ApiError.INVALID_KEY, (result as ApiResult.Failure).errorCode)
    }

    @Test
    fun testSuccessSignInFinish() {
        val result = authApi!!.finish("79000000000", ACCOUNT_KEY_HASH)

        assertTrue(result is ApiResult.Success)
        assertEquals(TOKEN, (result as ApiResult.Success).data)
    }
}