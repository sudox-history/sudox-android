package com.sudox.protocol.helpers

import com.sudox.common.structures.QueueList
import java.nio.ByteBuffer

internal const val BITS_IN_BYTE = 8
internal const val SIGNED_VALUE_MASK = 0xFF
internal const val LENGTH_HEADER_SIZE_IN_BYTES = 2

fun serializePacket(slices: Array<out ByteArray>): ByteBuffer {
    val slicesLength = slices.sumBy { it.size + LENGTH_HEADER_SIZE_IN_BYTES }
    val packetLength = slicesLength + LENGTH_HEADER_SIZE_IN_BYTES
    val buffer = ByteBuffer.allocateDirect(packetLength)

    buffer.putIntBE(packetLength, LENGTH_HEADER_SIZE_IN_BYTES)
    slices.forEach { buffer.putPacketSlice(it) }
    buffer.flip()

    return buffer
}

fun deserializePacket(buffer: ByteBuffer): QueueList<ByteArray>? {
    val slices = QueueList<ByteArray>()
    val length = buffer.limit()
    var index = 0

    while (index + LENGTH_HEADER_SIZE_IN_BYTES <= length) {
        val slice = buffer.readPacketSlice() ?: return null
        slices.push(slice)

        index += LENGTH_HEADER_SIZE_IN_BYTES
        index += slice.size
    }

    return slices
}

fun ByteBuffer.putPacketSlice(it: ByteArray) {
    putIntBE(it.size, LENGTH_HEADER_SIZE_IN_BYTES)
    put(it)
}

fun ByteBuffer.readPacketSlice(): ByteArray? {
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