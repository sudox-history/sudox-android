package com.sudox.messenger.api.auth.signin

import com.sudox.messenger.api.Api
import com.sudox.messenger.api.auth.AuthApi
import com.sudox.messenger.api.common.ApiResult

class SignInApiImpl(
        val api: Api,
        val authApi: AuthApi
) : SignInApi() {

    override fun confirmPhone(phoneCode: Int): ApiResult<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finish(hash: ByteArray): ApiResult<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}