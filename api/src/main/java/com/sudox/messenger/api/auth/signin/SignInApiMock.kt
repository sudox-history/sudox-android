package com.sudox.messenger.api.auth.signin

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.DISCONNECT_EVENT_NAME
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.auth.AuthApiMock
import com.sudox.messenger.api.common.ApiError
import com.sudox.messenger.api.common.ApiResult
import com.sudox.messenger.api.common.PHONE_REGEX
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

internal const val ACCOUNT_KEY_LENGTH = 97
internal val ACCOUNT_KEY = Random.nextBytes(ACCOUNT_KEY_LENGTH)

class SignInApiMock(
        val api: Api,
        val authApi: AuthApi
) : SignInApi() {

    var scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    val tasks = ArrayList<ScheduledFuture<*>>()

    init {
        api.eventEmitter.on(DISCONNECT_EVENT_NAME) {
            tasks.forEach { it.cancel(true) }
        }
    }

    override fun confirmPhone(phoneCode: Int, publicKey: ByteArray): ApiResult<Int> {
        val authMock = authApi as AuthApiMock

        return if (!api.isConnected()) {
            ApiResult.Failure(ApiError.NOT_CONNECTED)
        } else if (authApi.currentPhone != null || !PHONE_REGEX.matches(authApi.currentPhone!!)) {
            ApiResult.Failure(ApiError.INVALID_FORMAT)
        } else if (isPhoneInvalid(authMock, authApi.currentPhone!!)) {
            ApiResult.Failure(ApiError.INVALID_PHONE)
        } else if (phoneCode !in EXCHANGE_ACCEPTED_PHONE_CODE_MIN..EXCHANGE_DENIED_PHONE_CODE_MAX) {
            ApiResult.Failure(ApiError.INVALID_CODE)
        } else {
            tasks.add(scheduler.schedule({
                if (phoneCode in EXCHANGE_ACCEPTED_PHONE_CODE_MIN..EXCHANGE_ACCEPTED_PHONE_CODE_MAX) {
                    eventEmitter.emit(EXCHANGE_ACCEPTED_EVENT_NAME, ACCOUNT_KEY)
                } else if (phoneCode in EXCHANGE_DENIED_PHONE_CODE_MIN..EXCHANGE_DENIED_PHONE_CODE_MAX) {
                    eventEmitter.emit(EXCHANGE_DENIED_EVENT_NAME)
                }
            }, EXCHANGE_RESPONSE_DELAY, TimeUnit.MILLISECONDS))

            ApiResult.Success(Random.nextInt(EXCHANGE_CODE_MIN, EXCHANGE_CODE_MAX))
        }
    }

    override fun finish(hash: ByteArray): ApiResult<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun isPhoneInvalid(authMock: AuthApiMock, phone: String): Boolean {
        return !authMock.registeredPhones.contains(phone) ||
                !authMock.authSessions.containsKey(phone) ||
                authMock.authSessions[phone]!!
    }
}