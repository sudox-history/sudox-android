package com.sudox.protocol.model

interface ResponseCallback<T> {
    fun onMessage(response: T)
}