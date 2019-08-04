package com.sudox.messenger.api.auth

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.common.ApiError
import com.sudox.messenger.api.common.ApiResult
import com.sudox.messenger.api.common.PHONE_REGEX

class AuthApiMock(val api: Api) : AuthApi() {

    val authSessions = HashMap<String, Boolean>()
    val registeredPhones = ArrayList<String>().apply {
        add("79111111111")
    }

    override fun start(phone: String): ApiResult<Boolean> {
        return if (!api.isConnected()) {
            ApiResult.Failure(ApiError.NOT_CONNECTED)
        } else if (!PHONE_REGEX.matches(phone)) {
            ApiResult.Failure(ApiError.INVALID_FORMAT)
        } else if (authSessions.containsKey(phone)) {
            ApiResult.Failure(ApiError.INVALID_PHONE)
        } else {
            authSessions[phone] = false
            currentPhone = phone

            ApiResult.Success(registeredPhones.contains(phone))
        }
    }
}