package com.sudox.messenger.api.auth

import com.sudox.events.EventEmitter
import com.sudox.messenger.api.common.ApiResult

internal const val AUTH_STARTED_EVENT_NAME = "auth-success:started"

abstract class AuthApi {

    var currentPhone: String? = null
    val eventEmitter = EventEmitter()

    abstract fun start(phone: String): ApiResult<Boolean>
}