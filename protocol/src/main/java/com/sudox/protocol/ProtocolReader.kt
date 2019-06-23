package com.sudox.protocol

import com.sudox.protocol.helpers.LENGTH_HEADER_SIZE_IN_BYTES
import com.sudox.protocol.helpers.readIntBE
import com.sudox.sockets.SocketClient
import java.nio.ByteBuffer

class ProtocolReader(val socketClient: SocketClient) {

    private var slicesLengthInBytes: Int = 0
    private var slicesBuffer: ByteBuffer? = null

    fun readPacketBytes(): ByteBuffer? {
        if (slicesBuffer?.remaining() == 0) {
            resetPacket()
        }

        if (isNewPacket()) {
            readHeaders()
        }

        if (slicesLengthInBytes > 0) {
            readSlices()

            return if (slicesBuffer?.remaining() == 0) {
                return slicesBuffer!!.apply { flip() }
            } else {
                null
            }
        }

        return null
    }

    fun resetPacket() {
        slicesBuffer?.clear()
        slicesLengthInBytes = 0
    }

    private fun readHeaders() {
        slicesLengthInBytes = Math.max(readLength(), 0)

        if (slicesLengthInBytes > 0) {
            slicesBuffer = ByteBuffer.allocateDirect(slicesLengthInBytes)
        }
    }

    private fun readSlices() {
        val availableBytes = socketClient.availableBytes()
        val readCount = Math.min(availableBytes, slicesBuffer!!.remaining())

        if (readCount > 0) {
            socketClient.readToByteBuffer(slicesBuffer!!, readCount, slicesBuffer!!.position())
        }
    }

    private fun readLength(): Int {
        return socketClient
                .readBytes(LENGTH_HEADER_SIZE_IN_BYTES)
                .readIntBE()
    }

    private fun isNewPacket(): Boolean {
        return slicesLengthInBytes <= 0 && socketClient.availableBytes() >= LENGTH_HEADER_SIZE_IN_BYTES
    }
}