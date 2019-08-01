package com.sudox.messenger.api.core

import com.sudox.events.SingleEventEmitter

abstract class ApiCore {

    val statusEventEmitter = SingleEventEmitter()

    abstract fun startConnection()
    abstract fun stopConnection()
}