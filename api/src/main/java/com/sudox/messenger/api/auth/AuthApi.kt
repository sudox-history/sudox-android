package com.sudox.messenger.api.auth

import com.sudox.messenger.api.ApiResult
import java.util.concurrent.CompletableFuture

interface AuthApi {
    fun start(phone: String): ApiResult<Boolean>
    fun confirmPhone(phone: String, phoneCode: String, publicKey: ByteArray): ApiResult<Int>
    fun confirmPhone(phone: String, phoneCode: String): ApiResult<Boolean>
    fun finish(phone: String, name: String, nickname: String, hash: String): ApiResult<String>
    fun finish(phone: String, hash: String): ApiResult<String>
}