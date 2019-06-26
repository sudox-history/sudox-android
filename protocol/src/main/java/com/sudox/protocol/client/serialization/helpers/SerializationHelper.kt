package com.sudox.protocol.client.serialization.helpers

import com.sudox.common.structures.QueueList
import java.nio.ByteBuffer

internal const val BITS_IN_BYTE = 8
internal const val SIGNED_VALUE_MASK = 0xFF
internal const val LENGTH_HEADER_SIZE_IN_BYTES = 2

fun serializePacket(parts: Array<out ByteArray>): ByteBuffer {
    val partsLength = parts.sumBy { it.size + LENGTH_HEADER_SIZE_IN_BYTES }
    val packetLength = partsLength + LENGTH_HEADER_SIZE_IN_BYTES
    val buffer = ByteBuffer.allocateDirect(packetLength)

    buffer.putIntBE(partsLength, LENGTH_HEADER_SIZE_IN_BYTES)
    parts.forEach { buffer.putPacketPart(it) }
    buffer.flip()

    return buffer
}

fun deserializePacket(buffer: ByteBuffer): QueueList<ByteArray>? {
    val parts = QueueList<ByteArray>()
    val length = buffer.limit()
    var index = 0

    while (index + LENGTH_HEADER_SIZE_IN_BYTES <= length) {
        val part = buffer.readPacketPart() ?: return null
        parts.push(part)

        index += LENGTH_HEADER_SIZE_IN_BYTES
        index += part.size
    }

    return parts
}

fun ByteBuffer.putPacketPart(it: ByteArray) {
    putIntBE(it.size, LENGTH_HEADER_SIZE_IN_BYTES)
    put(it)
}

fun ByteBuffer.readPacketPart(): ByteArray? {
    val length = readIntBE(LENGTH_HEADER_SIZE_IN_BYTES)

    return if (length > 0 && remaining() >= length) {
        ByteArray(length).apply { get(this) }
    } else {
        null
    }
}

fun ByteBuffer.readIntBE(n: Int): Int {
    return ByteArray(n)
            .apply { get(this) }
            .readIntBE()
}

fun ByteBuffer.putIntBE(value: Int, n: Int) {
    val bytes = ByteArray(n)
    var index = 0

    for (i in n - 1 downTo 0) {
        bytes[index++] = (value shr (i * BITS_IN_BYTE)).toByte()
    }

    put(bytes)
}

fun ByteArray.readIntBE(): Int {
    var bytesIndex = 0
    var value = 0

    for (i in size - 1 downTo 0) {
        value = value or if (i > 0) {
            (this[bytesIndex++].toInt() shl (i * BITS_IN_BYTE))
        } else {
            (this[bytesIndex++].toInt() and SIGNED_VALUE_MASK)
        }
    }

    return value
}