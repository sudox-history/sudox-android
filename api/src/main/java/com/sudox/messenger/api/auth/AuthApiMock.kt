package com.sudox.messenger.api.auth

import com.sudox.messenger.api.ApiError
import com.sudox.messenger.api.ApiResult
import java9.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

internal const val PHONE_CODE_WHEN_EXCHANGE_ACCEPTED = "12345"
internal const val PHONE_CODE_WHEN_EXCHANGE_NOT_ACCEPTED = "56789"

internal const val EXCHANGE_CODE = 12345
internal const val EXCHANGE_TIME = 3000L

class AuthApiMock : AuthApi() {

    private var registeredPhones = ArrayList<String>()

    override fun start(phone: String): ApiResult<Boolean> {
        return if (registeredPhones.contains(phone)) {
            ApiResult.Success(true)
        } else {
            ApiResult.Success(false)
        }
    }

    override fun confirmPhone(phone: String, phoneCode: String, publicKey: ByteArray): ApiResult<Int> {
        if (isPhoneCodeValid(phoneCode)) {
            return ApiResult.Failure(ApiError.CODE_NOT_INVALID)
        }

        CompletableFuture.delayedExecutor(EXCHANGE_TIME, TimeUnit.MILLISECONDS) {
            if (phoneCode == PHONE_CODE_WHEN_EXCHANGE_ACCEPTED) {
                authEventEmitter.emit(AUTH_EXCHANGE_ACCEPTED_EVENT)
            } else if (phoneCode == PHONE_CODE_WHEN_EXCHANGE_NOT_ACCEPTED) {
                authEventEmitter.emit(AUTH_EXCHANGE_NOT_ACCEPTED_EVENT)
            }
        }

        return ApiResult.Success(EXCHANGE_CODE)
    }

    override fun confirmPhone(phone: String, phoneCode: String): ApiResult<Nothing> {
        return if (isPhoneCodeValid(phoneCode)) {
            ApiResult.Failure(ApiError.CODE_NOT_INVALID)
        } else {
            ApiResult.Success(null)
        }
    }

    override fun finish(phone: String, name: String, nickname: String, hash: String): ApiResult<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finish(phone: String, hash: String): ApiResult<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun isPhoneCodeValid(phoneCode: String): Boolean {
        return phoneCode != PHONE_CODE_WHEN_EXCHANGE_ACCEPTED && phoneCode != PHONE_CODE_WHEN_EXCHANGE_NOT_ACCEPTED
    }
}