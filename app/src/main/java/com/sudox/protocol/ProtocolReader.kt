package com.sudox.protocol

import com.sudox.protocol.impls.SeparatedBufferedReader
import java.io.EOFException
import java.lang.Exception
import java.net.SocketException

class ProtocolReader(val client: ProtocolClient) : Thread("SSTP Reader") {

    override fun run() {
        try {
            val reader = SeparatedBufferedReader(client.socket!!.getInputStream().reader(), ']')

            // Чтение ...
            while (client.isValid() && !isInterrupted) {
                try {
                    val line = reader.readLine()

                    // reader.readLine() блокирует поток до получения сообщения ...

                    client.controller!!.onPacket(line)
                } catch (e: EOFException) {
                    break
                } catch (e: SocketException) {
                    break
                }
            }
        } catch (e: Exception) {
            // Ignore
        }

        // Поток данных завершен.

        client.controller!!.onEnd()
    }
}