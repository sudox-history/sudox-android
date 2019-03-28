package com.sudox.protocol.helpers

/**
 * Удаляет нули в начале массива байтов.
 *
 * @param bytes - байты для обработки.
 */
fun removeLeadingZeros(bytes: ByteArray): ByteArray {
    if (bytes.isNotEmpty()) {
        var offset = 0

        // Calculating the zeros
        while (offset < bytes.size && bytes[offset].toInt() == 0) {
            offset++
        }

        // Copy part without zeros
        return ByteArray(bytes.size - offset).apply {
            System.arraycopy(bytes, offset, this, 0, bytes.size - offset)
        }
    }

    return bytes
}