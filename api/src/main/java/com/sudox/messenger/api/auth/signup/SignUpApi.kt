package com.sudox.messenger.api.auth.signup

import com.sudox.messenger.api.common.ApiResult

interface SignUpApi {
    fun confirmPhone(phoneCode: Int): ApiResult<Nothing>
    fun finish(phone: String, nickname: String, hash: ByteArray): ApiResult<String>
}