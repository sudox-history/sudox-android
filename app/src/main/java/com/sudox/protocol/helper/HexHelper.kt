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

    // Algorithm data
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

fun decodeHexString(input: String): String {
    val inputBytes = input.toByteArray()

    // Decode the input bytes
    val resultBytes = decodeHex(inputBytes)

    // Convert bytes to the string and return result
    return String(resultBytes)
}

fun encodeHex(input: ByteArray): ByteArray {
    val length = input.size
    val output = ByteArray(length shl 1)

    // Algorithm data
    var loopIndex = 0
    var cursorIndex = 0

    while (loopIndex < length) {
        output[cursorIndex++] = HEX_CHARS[(0xF0 and input[loopIndex].toInt()) ushr 4]
                .toByte()

        output[cursorIndex++] = HEX_CHARS[0x0F and input[loopIndex].toInt()]
                .toByte()

        // Increment loop index
        loopIndex++
    }

    return output
}

fun encodeHexString(input: String): String {
    val inputBytes = input.toByteArray()

    // Encode the input bytes
    val resultBytes = encodeHex(inputBytes)

    // Convert bytes to the string and return result
    return String(resultBytes)
}