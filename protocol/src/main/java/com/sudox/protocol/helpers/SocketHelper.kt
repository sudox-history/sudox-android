package com.sudox.protocol.helpers

import java.io.BufferedInputStream
import java.io.IOException

/**
 * Читает поток данных.
 * Если поток данных был закрыт, то возвращает false
 *
 * @param buffer - буффер, куда будут сохранены байты.
 */
fun BufferedInputStream.readData(buffer: ByteArray): Boolean {
    return try {
        // Stream has been closed
        read(buffer, 0, buffer.size) != -1
    } catch (e: IOException) {
        // Handle stream.close()
        false
    }
}