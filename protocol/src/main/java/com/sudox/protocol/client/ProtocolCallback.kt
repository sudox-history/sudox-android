package com.sudox.protocol.client

interface ProtocolCallback {
    fun onMessage(message: ByteArray)
    fun onStarted()
    fun onEnded()
}