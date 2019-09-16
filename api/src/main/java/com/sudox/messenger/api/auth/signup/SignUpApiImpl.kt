package com.sudox.messenger.api.auth.signup

import com.sudox.messenger.api.common.ApiResult

class SignUpApiImpl : SignUpApi {

    override fun confirmPhone(phoneCode: Int): ApiResult<Nothing> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finish(phone: String, nickname: String, hash: ByteArray): ApiResult<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}