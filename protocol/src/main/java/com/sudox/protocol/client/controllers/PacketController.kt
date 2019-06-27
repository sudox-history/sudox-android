package com.sudox.protocol.client.controllers

import com.sudox.protocol.client.network.SocketClient
import com.sudox.protocol.client.serialization.Deserializer
import com.sudox.protocol.client.serialization.Serializer
import java.nio.ByteBuffer

internal const val LENGTH_HEADER_SIZE = 2

class PacketController(val socketClient: SocketClient) {

    private var readLength: Int = 0
    private var readBuffer: ByteBuffer? = null
    private var serializer = Serializer()
    private var deserializer = Deserializer()

    fun readPacket(): Any? {
        if (readBuffer?.remaining() == 0) {
            resetReading()
        }

        if (readLength <= 0 && socketClient.available() >= LENGTH_HEADER_SIZE) {
            readLength()
        }

        if (readLength <= 0) {
            return null
        }

        readParts()

        return if (readBuffer?.remaining() == 0) {
            val result = deserializer.deserialize(readBuffer!!.apply {
                flip()
            })

            resetReading()
            return result
        } else {
            null
        }
    }

    fun sendPacket(parts: Array<Any>, urgent: Boolean = false) {
        val buffer = serializer.serialize(parts, LENGTH_HEADER_SIZE)

        val length = buffer.limit() - LENGTH_HEADER_SIZE
        val lengthFirstByte = length.toByte()
        val lengthSecondByte = (length shl 8).toByte()
        buffer.rewind()
        buffer.put(lengthFirstByte)
        buffer.put(lengthSecondByte)

        socketClient.send(buffer, urgent)
    }

    private fun readLength() {
        val bytes = socketClient.read(LENGTH_HEADER_SIZE)
        val firstByte = bytes[0].toInt()
        val secondByte = bytes[0].toInt() shl 8

        readLength = firstByte or secondByte
        readBuffer = ByteBuffer.allocateDirect(readLength)
    }

    private fun readParts() {
        val available = socketClient.available()
        val need = Math.min(available, readBuffer!!.remaining())

        if (need > 0) {
            socketClient.read(readBuffer!!, need, readBuffer!!.position())
        }
    }

    fun resetReading() {
        readBuffer?.clear()
        readBuffer = null
        readLength = 0
    }
}