package com.sudox.messenger.api

sealed class ApiResult<T : Any> {
    data class Success<T : Any>(val data: T?) : ApiResult<T>()
    data class Failure(@ApiError val errorCode: Int) : ApiResult<Nothing>()
}