package com.sudox.messenger.api.auth.signin

import com.sudox.messenger.api.common.ApiResult

internal const val EXCHANGE_ACCEPTED_EVENT_NAME = "auth-success:exchange-accepted"
internal const val EXCHANGE_DENIED_EVENT_NAME = "auth-error:exchange-denied"
internal const val EXCHANGE_DROPPED_EVENT_NAME = "auth-error:exchange-failed"

interface SignInApi {
    fun confirmPhone(phoneCode: Int): ApiResult<Int>
    fun finish(hash: ByteArray): ApiResult<String>
}