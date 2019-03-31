package com.sudox.protocol

import java.io.BufferedOutputStream
import java.util.concurrent.LinkedBlockingQueue

/**
 * Поток для записи данных в сокет.
 * Очередь отправки чистится после обрыва соединения.
 *
 * @param client - клиент протокола.
 */
class ProtocolWriter(private val client: ProtocolClient) : Thread("SSTP Writer") {

    // Очередь сообщений на отправку.
    private var messagesQueue: LinkedBlockingQueue<ByteArray> = LinkedBlockingQueue()

    /**
     * Основной рабочий метод потока.
     */
    override fun run() {
        val stream = BufferedOutputStream(client.socket!!.getOutputStream())

        // Writing ...
        while (!isInterrupted && client.isValid()) {
            try {
                val message = messagesQueue.take() ?: continue

                // Write message ...
                stream.write(message)
                stream.flush()
            } catch (e: Exception) {
                break
            }
        }

        // Clean queue ...
        messagesQueue.clear()
    }

    /**
     * Добавляет массив байтов в конец очереди на отправку.
     *
     * @param byteArray - массив байтов для отправки.
     */
    fun addToQueue(byteArray: ByteArray) {
        try {
            messagesQueue.offer(byteArray)
        } catch (e: InterruptedException) {
            // Ignore
        }
    }
}