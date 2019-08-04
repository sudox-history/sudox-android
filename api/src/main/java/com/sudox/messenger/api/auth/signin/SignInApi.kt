package com.sudox.messenger.api.auth.signin

import com.sudox.events.EventEmitter
import com.sudox.messenger.api.common.ApiResult

internal const val EXCHANGE_ACCEPTED_EVENT_NAME = "exchange-accepted"
internal const val EXCHANGE_DENIED_EVENT_NAME = "exchange-denied"

abstract class SignInApi {

    val eventEmitter = EventEmitter()

    abstract fun confirmPhone(phoneCode: Int, publicKey: ByteArray): ApiResult<Int>
    abstract fun finish(hash: ByteArray): ApiResult<String>
}