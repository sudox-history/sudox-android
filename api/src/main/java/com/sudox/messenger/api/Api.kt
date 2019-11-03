package com.sudox.messenger.api

import com.sudox.events.EventEmitter
import com.sudox.messenger.api.common.ApiResult

internal const val API_CONNECT_EVENT_NAME = "connect"
internal const val API_DISCONNECT_EVENT_NAME = "disconnect"

@SuppressWarnings("MagicNumber")
val supportedRegions = hashMapOf(
        "RU" to 7
)

abstract class Api {

    val eventEmitter = EventEmitter()

    abstract fun startConnection()
    abstract fun endConnection()
    abstract fun isConnected(): Boolean
    abstract fun <T : Any> makeRequest(event: String, vararg data: Any?): ApiResult<T>
}