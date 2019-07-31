package com.sudox.messenger.api.auth

import com.sudox.messenger.api.ApiResult

class AuthApiMock : AuthApi {

    private var registeredPhones = ArrayList<String>()

    override fun start(phone: String): ApiResult<Boolean> {
        return if (registeredPhones.contains(phone)) {
            ApiResult.Success(true)
        } else {
            ApiResult.Success(false)
        }
    }

    override fun confirmPhone(phone: String, phoneCode: String, publicKey: ByteArray): ApiResult<Int> {
        return if 
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