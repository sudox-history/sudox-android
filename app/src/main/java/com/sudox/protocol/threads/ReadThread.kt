package com.sudox.protocol.threads

import com.sudox.protocol.implementations.SeparatedBufferedReader
import java.io.EOFException
import java.net.Socket
import java.net.SocketException

class ReadThread(private val socket: Socket,
                 private val readCallback: (String) -> (Unit),
                 private val endCallback: () -> (Unit)) : Thread() {

    private val reader = SeparatedBufferedReader(socket.getInputStream().reader(), ']')

    override fun run() {
        try {
            doWork()
        } catch (e: EOFException) {
            endCallback()
        } catch (e: SocketException) {
            endCallback()
        }
    }

    private fun doWork() {
        while (!socket.isClosed && !isInterrupted) {
            readCallback(reader.readLine())
        }

        endCallback()
    }
}