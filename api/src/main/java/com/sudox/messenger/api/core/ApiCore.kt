package com.sudox.messenger.api.core

import com.sudox.events.SingleEventEmitter
import com.sudox.messenger.api.ApiError
import com.sudox.messenger.api.ApiResult

abstract class ApiCore {

    val statusEventEmitter = SingleEventEmitter()

    abstract fun start()
    abstract fun stop()
    abstract fun connected(): Boolean

    inline fun <T : Any> request(request: () -> ApiResult<T>): ApiResult<T> {
        if (!connected()) {
            return ApiResult.Failure(ApiError.NOT_CONNECTED)
        }

        return request()
    }
}