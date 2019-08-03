package com.sudox.messenger.api.core

import com.sudox.messenger.api.ApiStatus

class ApiCoreMock : ApiCore() {

    private var connected = false

    override fun start() {
        connected = true
        statusEventEmitter.emit(ApiStatus.CONNECTION_INSTALLED)
    }

    override fun stop() {
        connected = false
        statusEventEmitter.emit(ApiStatus.CONNECTION_CLOSED)
    }

    override fun connected(): Boolean {
        return connected
    }
}