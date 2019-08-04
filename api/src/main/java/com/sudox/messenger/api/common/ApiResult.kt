package com.sudox.messenger.api.common

sealed class ApiResult<out T : Any> {
    data class Success<T : Any>(val data: T?) : ApiResult<T>()
    data class Failure(@ApiError val errorCode: Int) : ApiResult<Nothing>()
}