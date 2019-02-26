package com.sudox.protocol

import java.io.DataInputStream
import java.lang.Exception

class ProtocolReader(private val client: ProtocolClient) : Thread("SSTP Reader") {

    override fun run() {
        val socket = client.socket!!
        val controller = client.controller!!
        val inputStream = socket.getInputStream()
        val dataInputStream = DataInputStream(inputStream)
        var dataBuffer = ByteArray(socket.receiveBufferSize)
        val packetEndSeparator = ']'.toByte()
        val packetStartSeparator = '['.toByte()
        var packetBytesRead = 0

        // Blocking ...
        while (!isInterrupted && client.isValid()) {
            try {
                // End of packet
                if (packetBytesRead > 0 && dataBuffer[packetBytesRead - 1] == packetEndSeparator) {
                    controller.onPacket(String(dataBuffer))
                    dataBuffer = ByteArray(socket.receiveBufferSize)
                    packetBytesRead = 0
                    continue
                } else if (packetBytesRead > 0
                        && dataBuffer.firstOrNull() == packetStartSeparator
                        && dataBuffer[packetBytesRead - 1] == packetEndSeparator) {

                    dataBuffer = ByteArray(socket.receiveBufferSize)
                    packetBytesRead = 0
                }

                // Increase buffer size
                if (dataBuffer.firstOrNull() != 0.toByte()) {
                    val increasedBuffer = ByteArray(dataBuffer.size + socket.receiveBufferSize)

                    // Copy all bytes & rebind buffer
                    System.arraycopy(dataBuffer, 0, increasedBuffer, 0, dataBuffer.size)
                    dataBuffer = increasedBuffer
                }

                // Read chunk ...
                val bytesRead = dataInputStream.read(dataBuffer)

                // Handle end of stream
                if (bytesRead == -1) {
                    break
                }

                packetBytesRead += bytesRead
            } catch (e: Exception) {
                break
            }
        }

        // Solving SA-6 problem
        if (!isInterrupted) controller.onEnd()
    }
}