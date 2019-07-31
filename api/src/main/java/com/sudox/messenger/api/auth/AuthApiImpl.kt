package com.sudox.messenger.api.auth

import com.sudox.messenger.api.ApiResult

class AuthApiImpl : AuthApi {

    override fun start(phone: String): ApiResult<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun confirmPhone(phone: String, phoneCode: String, publicKey: ByteArray): ApiResult<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun confirmPhone(phone: String, phoneCode: String): ApiResult<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finish(phone: String, name: String, nickname: String, hash: String): ApiResult<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finish(phone: String, hash: String): ApiResult<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}