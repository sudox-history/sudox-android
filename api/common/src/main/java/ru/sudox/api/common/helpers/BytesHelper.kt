package ru.sudox.api.common.helpers

/**
 * Преобразует массив байтов в HEX-строку
 *
 * @return Строка, в которой закодированы байты в формате HEX
 */
fun ByteArray.toHexString() = joinToString("") {
    "%02x".format(it)
}

/**
 * Преобразует HEX-строку в массив байтов
 *
 * @return Массив байтов, который был закодирован в HEX
 */
fun String.toHexByteArray() = this.chunked(2).map {
    it.toInt(16).toByte()
}.toByteArray()