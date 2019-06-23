package com.sudox.protocol.client.controllers

import com.sudox.protocol.client.helpers.LENGTH_HEADER_SIZE_IN_BYTES
import com.sudox.protocol.client.helpers.readIntBE
import com.sudox.sockets.SocketClient
import java.nio.ByteBuffer

class PacketController(val socketClient: SocketClient) {

    private var partsLengthInBytes: Int = 0
    private var partsBuffer: ByteBuffer? = null

    fun readPacket(): ByteBuffer? {
        if (partsBuffer?.remaining() == 0) {
            reset()
        }

        if (partsLengthInBytes <= 0 && socketClient.availableBytes() >= LENGTH_HEADER_SIZE_IN_BYTES) {
            readHeaders()
        }

        if (partsLengthInBytes > 0) {
            readParts()

            return if (partsBuffer?.remaining() == 0) {
                return partsBuffer!!.apply { flip() }
            } else {
                null
            }
        }

        return null
    }

    fun reset() {
        partsBuffer?.clear()
        partsLengthInBytes = 0
    }

    private fun readHeaders() {
        partsLengthInBytes = Math.max(readLength(), 0)

        if (partsLengthInBytes > 0) {
            partsBuffer = ByteBuffer.allocateDirect(partsLengthInBytes)
        }
    }

    private fun readParts() {
        val availableBytes = socketClient.availableBytes()
        val readCount = Math.min(availableBytes, partsBuffer!!.remaining())

        if (readCount > 0) {
            socketClient.readToByteBuffer(partsBuffer!!, readCount, partsBuffer!!.position())
        }
    }

    private fun readLength(): Int {
        return socketClient
                .readBytes(LENGTH_HEADER_SIZE_IN_BYTES)
                .readIntBE()
    }
}