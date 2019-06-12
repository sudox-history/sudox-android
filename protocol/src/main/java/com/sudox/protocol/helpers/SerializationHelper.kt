package com.sudox.protocol.helpers

import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.util.LinkedList

const val BITS_IN_BYTE = 8
const val SIGNED_VALUE_MASK = 0xFF
const val LENGTH_HEADER_SIZE_IN_BYTES = 3

fun serializePacket(slices: Array<out ByteArray>): ByteBuffer {
    val slicesLength = slices.sumBy { it.size + LENGTH_HEADER_SIZE_IN_BYTES }
    val packetLength = slicesLength + LENGTH_HEADER_SIZE_IN_BYTES
    val buffer = ByteBuffer.allocateDirect(packetLength)

    buffer.writeIntBE(packetLength, LENGTH_HEADER_SIZE_IN_BYTES)

    for (slice in slices) {
        buffer.writePacketSlice(slice)
    }

    buffer.flip()

    return buffer
}

/**
 * Returns list of slices if deserialization successfully completed.
 * Returns null if an error occurs.
 */
fun deserializePacketSlices(buffer: ByteBuffer): LinkedList<ByteArray>? {
    try {
        val slices = LinkedList<ByteArray>()
        var currentIndex = 0

        while (currentIndex + LENGTH_HEADER_SIZE_IN_BYTES <= buffer.limit()) {
            val slice = buffer.readPacketSlice() ?: return null

            slices.addLast(slice)
            currentIndex += LENGTH_HEADER_SIZE_IN_BYTES
            currentIndex += slice.size
        }

        return slices
    } catch (e: BufferUnderflowException) {
        return null
    }
}

fun ByteBuffer.writePacketSlice(it: ByteArray) {
    writeIntBE(it.size, LENGTH_HEADER_SIZE_IN_BYTES)
    put(it)
}

/**
 * Returns bytes of slice if deserialization successfully completed.
 * Returns null if an error occurs.
 */
fun ByteBuffer.readPacketSlice(): ByteArray? {
    val length = readIntBE(LENGTH_HEADER_SIZE_IN_BYTES)

    return if (length > 0) {
        ByteArray(length).apply { get(this) }
    } else {
        null
    }
}

fun ByteArray.toIntFromBE(): Int {
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

fun ByteBuffer.readIntBE(n: Int): Int {
    return ByteArray(n)
            .apply { get(this) }
            .toIntFromBE()
}

fun Int.toBytesBE(n: Int): ByteArray {
    val bytes = ByteArray(n)
    var bytesIndex = 0

    for (i in n - 1 downTo 0) {
        bytes[bytesIndex++] = (this shr (i * BITS_IN_BYTE)).toByte()
    }

    return bytes
}

fun ByteBuffer.writeIntBE(value: Int, n: Int) {
    put(value.toBytesBE(n))
}