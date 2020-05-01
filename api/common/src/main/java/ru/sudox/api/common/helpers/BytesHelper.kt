package ru.sudox.api.common.helpers

private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

/**
 * Преобразует массив байтов в HEX-строку
 *
 * @return Строка, в которой закодированы байты в формате HEX
 */
fun ByteArray.toHexString() = joinToString("") {
    "%02x".format(it)
}