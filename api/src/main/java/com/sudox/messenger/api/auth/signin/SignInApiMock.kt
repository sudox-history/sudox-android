package com.sudox.messenger.api.auth.signin

import com.sudox.messenger.api.API_DISCONNECT_EVENT_NAME
import com.sudox.messenger.api.Api
import com.sudox.messenger.api.auth.AUTH_STARTED_EVENT_NAME
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.auth.AuthApiMock
import com.sudox.messenger.api.common.ApiError
import com.sudox.messenger.api.common.ApiResult
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

internal const val EXCHANGE_ACCEPTED_PHONE_CODE_MIN = 10000
internal const val EXCHANGE_ACCEPTED_PHONE_CODE_MAX = 19999
internal const val EXCHANGE_DENIED_PHONE_CODE_MIN = 20000
internal const val EXCHANGE_DENIED_PHONE_CODE_MAX = 29999

internal const val EXCHANGE_CODE_MIN = 10000
internal const val EXCHANGE_CODE_MAX = 99999
internal const val EXCHANGE_RESPONSE_DELAY = 3000L

internal const val ACCOUNT_KEY_LENGTH = 24
internal const val ACCOUNT_KEY_HASH_LENGTH = 48
internal val ACCOUNT_KEY = ByteArray(ACCOUNT_KEY_LENGTH) { 0 }
internal val ACCOUNT_KEY_HASH = ByteArray(ACCOUNT_KEY_HASH_LENGTH) { 0 }

class SignInApiMock(
        val api: Api,
        val authApi: AuthApi
) : SignInApi {

    private var scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private val tasks = ArrayList<ScheduledFuture<*>>()

    init {
        listenDisconnect()
        listenAuthStart()
    }

    private fun listenDisconnect() = api.eventEmitter.on(API_DISCONNECT_EVENT_NAME) {
        cancelPendingExchangeTasks()
        authApi.eventEmitter.emit(EXCHANGE_DROPPED_EVENT_NAME)
    }

    private fun listenAuthStart() = authApi.eventEmitter.on(AUTH_STARTED_EVENT_NAME) {
        cancelPendingExchangeTasks()
    }

    private fun cancelPendingExchangeTasks() {
        tasks.forEach { it.cancel(true) }
    }

    override fun confirmPhone(phoneCode: Int): ApiResult<Int> {
        val authMock = authApi as AuthApiMock

        return if (!api.isConnected()) {
            ApiResult.Failure(ApiError.NOT_CONNECTED)
        } else if (authApi.currentPhone == null) {
            ApiResult.Failure(ApiError.INVALID_FORMAT)
        } else if (isPhoneInvalid(authMock, authApi.currentPhone!!) || authMock.authSessions[authApi.currentPhone!!]!!) {
            ApiResult.Failure(ApiError.INVALID_PHONE)
        } else if (phoneCode !in EXCHANGE_ACCEPTED_PHONE_CODE_MIN..EXCHANGE_DENIED_PHONE_CODE_MAX) {
            ApiResult.Failure(ApiError.INVALID_CODE)
        } else {
            authApi.authSessions[authApi.currentPhone!!] = true

            tasks.add(scheduler.schedule({
                if (phoneCode in EXCHANGE_ACCEPTED_PHONE_CODE_MIN..EXCHANGE_ACCEPTED_PHONE_CODE_MAX) {
                    authApi.eventEmitter.emit(EXCHANGE_ACCEPTED_EVENT_NAME, ACCOUNT_KEY)
                } else if (phoneCode in EXCHANGE_DENIED_PHONE_CODE_MIN..EXCHANGE_DENIED_PHONE_CODE_MAX) {
                    authApi.eventEmitter.emit(EXCHANGE_DENIED_EVENT_NAME)
                }
            }, EXCHANGE_RESPONSE_DELAY, TimeUnit.MILLISECONDS))

            ApiResult.Success(Random.nextInt(EXCHANGE_CODE_MIN, EXCHANGE_CODE_MAX))
        }
    }

    override fun finish(hash: ByteArray): ApiResult<String> {
        val authMock = authApi as AuthApiMock

        return if (!api.isConnected()) {
            ApiResult.Failure(ApiError.NOT_CONNECTED)
        } else if (authApi.currentPhone == null || isPhoneInvalid(authMock, authApi.currentPhone!!)) {
            ApiResult.Failure(ApiError.INVALID_PHONE)
        } else if (!hash.contentEquals(ACCOUNT_KEY_HASH)) {
            ApiResult.Failure(ApiError.INVALID_KEY)
        } else {
            authApi.currentToken = String(
                    Random.nextBytes(ACCOUNT_KEY_HASH_LENGTH)
            )

            ApiResult.Success(authApi.currentToken)
        }
    }

    private fun isPhoneInvalid(authMock: AuthApiMock, phone: String): Boolean {
        return  !authMock.registeredPhones.contains(phone) ||
                !authMock.authSessions.containsKey(phone)
    }
}