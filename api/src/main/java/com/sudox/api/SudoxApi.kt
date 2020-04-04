package com.sudox.api

import com.sudox.api.connections.Connection
import com.sudox.api.connections.ConnectionListener
import com.sudox.api.serializers.Serializer

class SudoxApi(
        val connection: Connection,
        val serializer: Serializer
) : ConnectionListener {

    init {
        connection.setListener(this)
    }

    override fun onStart() {
    }

    override fun onReceive(bytes: ByteArray) {
    }

    override fun onEnd() {
    }
}