package ru.sudox.api.common.helpers

import kotlin.experimental.and

private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

/**
 * Преобразует массив байтов в HEX-строку
 *
 * @return Строка, в которой закодированы байты в формате HEX
 */
fun ByteArray.toHexString(): String {
    val hexChars = CharArray(size * 2)

    for (j in indices) {
        val v = (this[j] and 0xFF.toByte()).toInt()

        hexChars[j * 2] = HEX_ARRAY[v ushr 4]
        hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
    }

    return String(hexChars)
}