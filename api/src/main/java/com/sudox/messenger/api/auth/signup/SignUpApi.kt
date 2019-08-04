package com.sudox.messenger.api.auth.signup

import com.sudox.messenger.api.common.ApiResult

interface SignUpApi {
    fun confirmPhone(phone: String, phoneCode: Int): ApiResult<Boolean>
    fun finish(phone: String, nickname: String, hash: ByteArray): ApiResult<String>
}