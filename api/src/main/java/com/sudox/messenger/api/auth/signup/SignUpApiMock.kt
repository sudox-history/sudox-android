package com.sudox.messenger.api.auth.signup

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.auth.AuthApiMock
import com.sudox.messenger.api.auth.signin.EXCHANGE_ACCEPTED_PHONE_CODE_MIN
import com.sudox.messenger.api.auth.signin.EXCHANGE_DENIED_PHONE_CODE_MAX
import com.sudox.messenger.api.common.ApiError
import com.sudox.messenger.api.common.ApiResult

class SignUpApiMock(
        val api: Api,
        val authApi: AuthApi
) : SignUpApi {

    override fun confirmPhone(phoneCode: Int): ApiResult<Nothing> {
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

            // Data not needed for this response.
            ApiResult.Success(null)
        }
    }

    override fun finish(phone: String, nickname: String, hash: ByteArray): ApiResult<String> {
        TODO()
    }

    private fun isPhoneInvalid(authMock: AuthApiMock, phone: String): Boolean {
        return authMock.registeredPhones.contains(phone) && authMock.authSessions.containsKey(phone)
    }
}