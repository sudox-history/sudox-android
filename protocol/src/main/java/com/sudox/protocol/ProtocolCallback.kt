package com.sudox.protocol

interface ProtocolCallback {
    fun onMessage(message: ByteArray)
    fun onStarted()
    fun onEnded()
}