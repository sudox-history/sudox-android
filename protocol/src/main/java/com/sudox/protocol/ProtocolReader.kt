package com.sudox.protocol

import com.sudox.protocol.helpers.LENGTH_HEADER_SIZE_IN_BYTES
import com.sudox.protocol.helpers.readIntBE
import com.sudox.sockets.SocketClient
import java.nio.ByteBuffer

class ProtocolReader(val socketClient: SocketClient) {

    private var packetDataLengthInBytes: Int = 0
    private var packetDataBuffer: ByteBuffer? = null

    fun readPacketBytes(): ByteBuffer? {
        if (packetDataBuffer?.remaining() == 0) {
            resetPacket()
        }

        if (isNewPacket()) {
            readPacketHeaders()
        }

        if (packetDataLengthInBytes > 0) {
            readPacketData()

            return if (packetDataBuffer?.remaining() == 0) {
                return packetDataBuffer!!.apply { flip() }
            } else {
                null
            }
        }

        return null
    }

    fun resetPacket() {
        packetDataBuffer?.clear()
        packetDataLengthInBytes = 0
    }

    private fun readPacketHeaders() {
        packetDataLengthInBytes = Math.max(readFullPacketLength() - LENGTH_HEADER_SIZE_IN_BYTES, 0)

        if (packetDataLengthInBytes > 0) {
            packetDataBuffer = ByteBuffer.allocateDirect(packetDataLengthInBytes)
        }
    }

    private fun readPacketData() {
        val availableBytes = socketClient.availableBytes()
        val readCount = Math.min(availableBytes, packetDataLengthInBytes)

        if (readCount > 0) {
            socketClient.readToByteBuffer(packetDataBuffer!!, readCount, packetDataBuffer!!.position())
        }
    }

    private fun readFullPacketLength(): Int {
        return socketClient
                .readBytes(LENGTH_HEADER_SIZE_IN_BYTES)
                .readIntBE()
    }

    private fun isNewPacket(): Boolean {
        return packetDataLengthInBytes <= 0 && socketClient.availableBytes() >= LENGTH_HEADER_SIZE_IN_BYTES
    }
}