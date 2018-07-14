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

fun decodeHex(input: ByteArray): ByteArray {
    val length = input.size
    val output = ByteArray(length shr 1)

    // Two characters from the hex value.
    var loopIndex = 0
    var cursorIndex = 0

    while (cursorIndex < length) {
        // Convert original char to hex
        val hexChar = (Character.digit(input[cursorIndex++].toChar(), 16) shl 4) or
                Character.digit(input[cursorIndex++].toChar(), 16)

        // Save hex char
        output[loopIndex] = (hexChar and 0xFF).toByte()

        // Increment loop index
        loopIndex++
    }

    return output
}