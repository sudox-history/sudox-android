package com.sudox.messenger.api.auth

import com.sudox.messenger.api.common.ApiResult

abstract class AuthApi {

    var currentPhone: String? = null

    abstract fun start(phone: String): ApiResult<Boolean>
}