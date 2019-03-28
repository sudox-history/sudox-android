package com.sudox.protocol

import com.sudox.protocol.helpers.readData
import java.io.BufferedInputStream

/**
 * Поток для чтения из сокета и контроля обрыва соединения.
 * Читает пакеты в следующей форме: [A][B][C]
 *
 * @param client - клиент протокола.
 */
class ProtocolReader(private val client: ProtocolClient) : Thread("SSTP Reader") {

    /**
     * Основной рабочий метод потока.
     */
    override fun run() {
        val controller = client.controller!!
        val stream = BufferedInputStream(client.socket!!.getInputStream())
        val builder = StringBuilder()

        // Reading ...
        while (!isInterrupted && client.isValid()) {
            val buffer = ByteArray(BUFFER_SIZE)

            // Handle socket closing ...
            if (!stream.readData(buffer)) {
                break
            }

            // Parse the data ...
            val line = String(buffer)
            val parts = PACKET_MATCH_REGEX
                    .findAll(line)
                    .toList()

            if (!parts.isEmpty()) {
                // Buffer contains a packet separator
                for (part in parts) {
                    builder.append(part.value)

                    // When packet completed
                    if (builder.endsWith("]")) {
                        controller.onPacket(builder.toString())
                        builder.clear()
                    }
                }
            } else {
                // Buffer contains only a full part of packet
                builder.append(line)
            }
        }

        // Clean memory ...
        stream.close()
        builder.clear()

        // Solving SA-6 problem
        if (!isInterrupted) controller.onEnd()
    }
}