package com.sudox.messenger.api.auth.signin

import com.sudox.events.EventEmitter
import com.sudox.messenger.api.common.ApiResult

internal const val EXCHANGE_ACCEPTED_EVENT_NAME = "exchange-accepted"
internal const val EXCHANGE_DENIED_EVENT_NAME = "exchange-denied"
internal const val EXCHANGE_DROPPED_EVENT_NAME = "exchange-failed"

abstract class SignInApi {

    val exchangeEventEmitter = EventEmitter()

    abstract fun confirmPhone(phoneCode: Int): ApiResult<Int>
    abstract fun finish(hash: ByteArray): ApiResult<String>
}