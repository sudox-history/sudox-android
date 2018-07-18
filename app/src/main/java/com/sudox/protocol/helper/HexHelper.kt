package com.sudox.protocol.helper

import java.util.*

// Hex chars
private val HEX_CHARS = arrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
)

fun randomHexString(length: Int): String {
    val random = Random(15)

    // StringBuilder for more efficiency
    val builder = StringBuilder()

    // Iterate the length
    for (i in 0 until length) {
        val hexCharIndex = random.nextInt(16)

        // Get the char of current index
        val hexChar = HEX_CHARS[hexCharIndex]

        // Add char to the builder
        builder.append(hexChar)
    }

    return builder.toString()
}

fun decodeHex(input: String): ByteArray {
    val length = input.length
    val output = ByteArray(length shr 1)

    // Algorithm data
    var loopIndex = 0
    var cursorIndex = 0

    while (cursorIndex < length) {
        // Convert original char to hex
        val hexChar = (Character.digit(input[cursorIndex++], 16) shl 4) or
                Character.digit(input[cursorIndex++], 16)

        // Save hex char
        output[loopIndex] = (hexChar and 0xFF).toByte()

        // Increment loop index
        loopIndex++
    }

    return output
}

fun encodeHex(input: String): ByteArray {
    return encodeHexBytes(input.toByteArray())
}

fun encodeHexBytes(bytes: ByteArray): ByteArray {
    val length = bytes.size
    val output = ByteArray(length shl 1)

    // Algorithm data
    var loopIndex = 0
    var cursorIndex = 0

    while (loopIndex < length) {
        output[cursorIndex++] = HEX_CHARS[(0xF0 and bytes[loopIndex].toInt()) ushr 4]
                .toByte()

        output[cursorIndex++] = HEX_CHARS[0x0F and bytes[loopIndex].toInt()]
                .toByte()

        // Increment loop index
        loopIndex++
    }

    return output
}