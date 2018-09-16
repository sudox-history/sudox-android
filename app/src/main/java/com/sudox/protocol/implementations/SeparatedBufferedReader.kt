package com.sudox.protocol.implementations

import java.io.BufferedReader
import java.io.EOFException
import java.io.Reader

class SeparatedBufferedReader(reader: Reader,
                              separator: Char) : BufferedReader(reader) {

    // Separator for detect the end of the message
    private val separatorNumber = separator.toInt()

    override fun readLine(): String = synchronized(lock) {
        val buffer = StringBuffer()

        while (true) {
            val byte = read() and 0xFF

            if (byte == 255) {
                // End of stream
                throw EOFException()
            } else {
                buffer.append(byte.toChar())

                // End of message
                if (byte == separatorNumber) {
                    break
                }
            }
        }

        return buffer.toString()
    }
}