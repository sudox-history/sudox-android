package com.sudox.protocol

import java.io.DataOutputStream
import java.util.concurrent.LinkedBlockingQueue

class ProtocolWriter(private val client: ProtocolClient) : Thread("SSTP Writer") {

    // Очередь сообщений
    internal var queue: LinkedBlockingQueue<ByteArray> = LinkedBlockingQueue()

    override fun run() {
        val socket = client.socket!!
        val outputStream = socket.getOutputStream()
        val dataOutputStream = DataOutputStream(outputStream)

        // Blocking ...
        while (!isInterrupted && client.isValid()) {
            try {
                val bytes = queue.take() ?: continue

                // Writing ...
                dataOutputStream.write(bytes)
                dataOutputStream.flush()
            } catch (e: Exception) {
                break
            }
        }

        // Clean memory ...
        dataOutputStream.close()
        queue.clear()
    }
}