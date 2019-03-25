package com.sudox.protocol.helpers

/**
 * Удаляет нули в начале массива байтов.
 *
 * @param bytes - байты для обработки.
 */
fun removeLeadingZeros(bytes: ByteArray): ByteArray {
    var i = 0

    while (i < bytes.size - 1) {
        if (bytes[i].toInt() != 0) {
            break
        }

        i++
    }

    return if (i == 0) {
        bytes
    } else {
        val stripped = ByteArray(bytes.size - i)
        System.arraycopy(bytes, i, stripped, 0, stripped.size)
        stripped
    }
}