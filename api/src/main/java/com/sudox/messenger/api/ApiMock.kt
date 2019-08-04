package com.sudox.messenger.api

import com.sudox.messenger.api.common.ApiResult
import java.lang.UnsupportedOperationException

class ApiMock : Api() {

    private var connected = false

    override fun startConnection() {
        connected = true
        eventEmitter.emit(API_CONNECT_EVENT_NAME)
    }

    override fun endConnection() {
        connected = false
        eventEmitter.emit(API_DISCONNECT_EVENT_NAME)
    }

    override fun isConnected(): Boolean {
        return connected
    }

    override fun <T : Any> makeRequest(event: String, vararg data: Any?): ApiResult<T> {
        throw UnsupportedOperationException("Can't be implemented on mocks.")
    }
}