package com.sudox.protocol.client.network

interface SocketCallback {
    fun socketConnected()
    fun socketClosed(error: Boolean)
    fun socketReceive()
}