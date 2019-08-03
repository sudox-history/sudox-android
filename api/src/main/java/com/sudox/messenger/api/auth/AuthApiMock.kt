package com.sudox.messenger.api.auth

import com.sudox.crypto.algorithms.SHA
import com.sudox.messenger.api.ApiError
import com.sudox.messenger.api.ApiResult
import java8.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

internal const val PHONE_REGISTERED = "79000000000"

internal const val PHONE_CODE_WHEN_EXCHANGE_ACCEPTED = "12345"
internal const val PHONE_CODE_WHEN_EXCHANGE_DENIED = "56789"

internal const val EXCHANGE_CODE = 12345
internal const val EXCHANGE_TIME = 3000L

internal const val PUBLIC_KEY_LENGTH = 97
internal const val ACCOUNT_KEY_LENGTH = 48
internal const val TOKEN_LENGTH = 32

internal val TOKEN = String(Random.nextBytes(TOKEN_LENGTH))
internal val ACCOUNT_KEY = Random.nextBytes(ACCOUNT_KEY_LENGTH)
internal val ACCOUNT_KEY_HASH = SHA.compute(ACCOUNT_KEY)
internal val NICKNAME_REGEX = Regex("^[-a-zA-Z0-9.!;&^\$*#()-+]{3,15}\$")
internal val PHONE_REGEX = Regex("^7[0-9]{10}\$")

class AuthApiMock : AuthApi() {

    private var registeredPhones = ArrayList<String>().apply {
        add(PHONE_REGISTERED)
    }

    override fun start(phone: String): ApiResult<Boolean> {
        return if (!PHONE_REGEX.matches(phone)) {
            ApiResult.Failure(ApiError.INVALID_FORMAT)
        } else {
            ApiResult.Success(registeredPhones.contains(phone))
        }
    }

    override fun confirmPhone(phone: String, phoneCode: String, publicKey: ByteArray): ApiResult<Int> {
        return if (!PHONE_REGEX.matches(phone) || publicKey.size != PUBLIC_KEY_LENGTH) {
            ApiResult.Failure(ApiError.INVALID_FORMAT)
        } else if (!isPhoneCodeValid(phoneCode)) {
            ApiResult.Failure(ApiError.INVALID_CODE)
        } else {
            CompletableFuture.delayedExecutor(EXCHANGE_TIME, TimeUnit.MILLISECONDS).execute {
                if (phoneCode == PHONE_CODE_WHEN_EXCHANGE_ACCEPTED) {
                    authEventEmitter.emit(AUTH_EXCHANGE_ACCEPTED_EVENT, ACCOUNT_KEY)
                } else if (phoneCode == PHONE_CODE_WHEN_EXCHANGE_DENIED) {
                    authEventEmitter.emit(AUTH_EXCHANGE_DENIED_EVENT)
                }
            }

            ApiResult.Success(EXCHANGE_CODE)
        }
    }

    override fun confirmPhone(phone: String, phoneCode: String): ApiResult<Nothing> {
        return if (!PHONE_REGEX.matches(phone)) {
            ApiResult.Failure(ApiError.INVALID_FORMAT)
        } else if (!isPhoneCodeValid(phoneCode)) {
            ApiResult.Failure(ApiError.INVALID_CODE)
        } else {
            ApiResult.Success(null)
        }
    }

    override fun finish(phone: String, nickname: String, hash: ByteArray): ApiResult<String> {
        if (!PHONE_REGEX.matches(phone) || !NICKNAME_REGEX.matches(nickname)) {
            return ApiResult.Failure(ApiError.INVALID_FORMAT)
        } else if (!hash.contentEquals(ACCOUNT_KEY_HASH)) {
            return ApiResult.Failure(ApiError.INVALID_KEY)
        }

        return ApiResult.Success(TOKEN)
    }

    override fun finish(phone: String, hash: ByteArray): ApiResult<String> {
        if (!PHONE_REGEX.matches(phone)) {
            return ApiResult.Failure(ApiError.INVALID_FORMAT)
        } else if (!hash.contentEquals(ACCOUNT_KEY_HASH)) {
            return ApiResult.Failure(ApiError.INVALID_KEY)
        }

        return ApiResult.Success(TOKEN)
    }

    private fun isPhoneCodeValid(phoneCode: String): Boolean {
        return phoneCode == PHONE_CODE_WHEN_EXCHANGE_ACCEPTED || phoneCode == PHONE_CODE_WHEN_EXCHANGE_DENIED
    }
}