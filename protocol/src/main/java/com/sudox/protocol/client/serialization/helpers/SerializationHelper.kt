package com.sudox.protocol.client.serialization.helpers

import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2

fun ByteBuffer.writeIntLE(value: Int, octets: Int, position: Int = 0) {
    for (i in 1..octets) {
        if (position > 0) {
            put(position + i - 1, (value ushr ((i - 1) * 8)).toByte())
        } else {
            put((value ushr ((i - 1) * 8)).toByte())
        }
    }
}

fun ByteBuffer.writeLongLE(value: Long, octets: Int, position: Int = 0) {
    for (i in 1..octets) {
        if (position > 0) {
            put(position + i - 1, (value ushr ((i - 1) * 8)).toByte())
        } else {
            put((value ushr ((i - 1) * 8)).toByte())
        }
    }
}

fun ByteBuffer.readUIntLE(octets: Int): Int {
    var value = 0

    for (i in 0 until octets) {
        value = value or ((get().toInt() and 0xFF) shl (8 * i))
    }

    return value
}

fun ByteBuffer.readLongLE(octets: Int): Long {
    var value = 0L

    for (i in 0 until octets) {
        var byte = get().toLong()

        if (i < octets - 1) {
            byte = byte and 0xFF
        }

        value = value or ((byte) shl (8 * i))
    }

    return value
}

fun Long.calculateOctets(): Int {
    if (this == 0L) {
        return 1
    } else if (this == Long.MAX_VALUE || this == Long.MIN_VALUE) {
        return 8
    }

    return if (this > 0) {
        floor((log2(toDouble()) + 1) / 8) + 1
    } else {
        ceil((log2(abs(this).toDouble()) + 1) / 8)
    }.toInt()
}