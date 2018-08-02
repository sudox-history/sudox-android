package com.sudox.protocol.model

interface ConnectionStateCallback {
    fun onReconnect(){}
    fun onConnect(){}
}