package com.sudox.protocol.implementations

import java.io.BufferedReader
import java.io.EOFException
import java.io.Reader
import java.lang.StringBuilder

class SeparatedBufferedReader(reader: Reader,
                              separator: Char) : BufferedReader(reader) {

    // Separator for detect the end of the message
    private val separatorNumber = separator.toInt()

    override fun readLine(): String = synchronized(lock) {
        val builder = StringBuilder()

        // Читаем строку
        readString(builder)

        // Возвращаем ответ
        return builder.toString()
    }

    private tailrec fun readString(builder: StringBuilder) {
        val byte = read() and 0xFF

        // Конец потока (закрытие соединение и т.п.)
        if (byte == 255) throw EOFException()

        // Сохраним символ
        builder.append(byte.toChar())

        // Читаем следующий байт
        if (byte != separatorNumber) {
            readString(builder)
        }
    }
}