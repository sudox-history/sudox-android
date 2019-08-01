package com.sudox.messenger.api.core

import com.sudox.messenger.api.ApiStatus

class ApiCoreMock : ApiCore() {

    override fun startConnection() {
        statusEventEmitter.emit(ApiStatus.CONNECTION_INSTALLED)
    }

    override fun stopConnection() {
        statusEventEmitter.emit(ApiStatus.CONNECTION_CLOSED)
    }
}