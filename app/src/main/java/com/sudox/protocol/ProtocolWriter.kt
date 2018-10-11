package com.sudox.protocol

import java.util.concurrent.LinkedBlockingQueue

class ProtocolWriter(val client: ProtocolClient) : Thread("SSTP Writer") {

    internal var messagesQueue: LinkedBlockingQueue<String> = LinkedBlockingQueue()

    override fun run() {
        try {
            val writer = client.socket!!.getOutputStream().bufferedWriter()

            // Работа потока ...
            while (!isInterrupted && client.isValid()) {
                try {
                    val message = messagesQueue.take() ?: continue

                    // Запись ...

                    writer.write(message)
                    writer.flush()
                } catch (e: Exception) {
                    break
                }
            }

            // Работа потока завершена ...
        } catch (e: Exception) {
            // Ignore, подловит поток чтения.
        }
    }
}