package com.sudox.protocol.model

interface MessageCallback {
    fun onMessage(jsonModel: JsonModel)
}