package com.sudox.api

import android.util.Log
import com.sudox.api.connections.Connection
import com.sudox.api.connections.ConnectionListener
import com.sudox.api.serializers.Serializer

class SudoxApi(
        val connection: Connection,
        val serializer: Serializer
) : ConnectionListener {

    init {
        connection.listener = this
    }

    /**
     * Устанавливает соединение с сервером
     */
    fun startConnection() {
        connection.start("sudox.ru", 5000)
    }

    /**
     * Прерывает соединение с сервером
     */
    fun endConnection() {
        connection.end()
    }

    override fun onStart() {
        Log.v("Sudox", "Connection started!")
        connection.sendData("Hello World".toByteArray())
    }

    override fun onReceive(bytes: ByteArray) {
        Log.v("Sudox", "Connection: ${String(bytes)}")
    }

    override fun onEnd() {
    }
}