package com.sudox.protocol

import com.sudox.protocol.helpers.LENGTH_HEADER_SIZE_IN_BYTES
import com.sudox.protocol.helpers.readIntBE
import com.sudox.sockets.SocketClient
import java.nio.ByteBuffer

class ProtocolReader(val socketClient: SocketClient) {

    private var partsLengthInBytes: Int = 0
    private var partsBuffer: ByteBuffer? = null

    fun readPacketBytes(): ByteBuffer? {
        if (partsBuffer?.remaining() == 0) {
            resetPacket()
        }

        if (isNewPacket()) {
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

    fun resetPacket() {
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

    private fun isNewPacket(): Boolean {
        return partsLengthInBytes <= 0 && socketClient.availableBytes() >= LENGTH_HEADER_SIZE_IN_BYTES
    }
}