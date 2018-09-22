package com.sudox.protocol.threads

import com.sudox.protocol.implementations.BlockingQueue
import java.net.Socket

class WriteThread(private val socket: Socket) : Thread() {

    internal val messagesQueue = BlockingQueue<String>()

    // Others IO variables
    private val writer = socket.getOutputStream().bufferedWriter()

    override fun run() {
        try {
            while (!socket.isClosed && !isInterrupted) {
                val firstMessage = messagesQueue.poll()

                // Send message ...
                writer.write(firstMessage)
                writer.flush()
            }

            messagesQueue.clear()
        } catch (e: InterruptedException) {
            // Ignore
        }
    }
}