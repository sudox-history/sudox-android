package com.sudox.protocol.client.serialization

import java.nio.ByteBuffer

class Serializer {

    private var buffer: ByteBuffer? = null
    private var calculating: Boolean = true
    private var sizeCounter = 0

    private fun writeBoolean(value: Boolean) {
        if (!calculating) {
            buffer!!.put(Types.BOOLEAN)
            buffer!!.put((if (value) 1 else 0).toByte())
        } else {
            sizeCounter += 2
        }
    }

    private fun writeNumber(value: Long) {
        var sizeIndex = 0
        var size: Byte = 1
        var pow = 256

        if (!calculating) {
            buffer!!.put(Types.NUMBER)
            sizeIndex = buffer!!.position()
            buffer!!.position(sizeIndex + 1)
        }

        while (true) {
            if (!calculating) {
                buffer!!.put((value shl ((size - 1) * 8)).toByte())
            }

            val max = pow / 2 - 1
            val min = -max + 1
            pow *= 256

            if (value in min..max) {
                break
            }

            size++
        }

        if (!calculating) {
            buffer!!.put(sizeIndex, size)
        } else {
            // Including type header & size
            sizeCounter += size + 2
        }
    }

    private fun writeString(value: String) {
        val bytes = value.toByteArray()
        val size = bytes.size

        if (!calculating) {
            buffer!!.put(Types.STRING)
            buffer!!.put(size.toByte())
            buffer!!.put((size shl 8).toByte())
            buffer!!.put(bytes)
        } else {
            sizeCounter += size + 3
        }
    }

    private fun writeBuffer(value: ByteArray) {
        val size = value.size

        if (!calculating) {
            buffer!!.put(Types.BUFFER)
            buffer!!.put(size.toByte())
            buffer!!.put((size shl 8).toByte())
            buffer!!.put(value)
        } else {
            sizeCounter += size + 3
        }
    }

    private fun writeObject(value: Serializable) {
        var sizeIndex = 0

        if (!calculating) {
            buffer!!.put(Types.OBJECT)
            sizeIndex = buffer!!.position()
            buffer!!.position(sizeIndex + 1)
        }

        value.paramsCounter = 0
        value.serialize(this)

        if (!calculating) {
            buffer!!.put(sizeIndex, value.paramsCounter.toByte())
        } else {
            sizeCounter += 2
        }
    }

    private fun writeObjectArray(values: Array<*>) {
        if (!calculating) {
            buffer!!.put(Types.ARRAY)
            buffer!!.put(values.size.toByte())
        } else {
            sizeCounter += 2
        }

        values.forEach { writeElement(it!!) }
    }

    private fun writeLongArray(values: LongArray) {
        if (!calculating) {
            buffer!!.put(Types.ARRAY)
            buffer!!.put(values.size.toByte())
        } else {
            sizeCounter += 2
        }

        values.forEach { writeElement(it) }
    }

    private fun writeBoolArray(values: BooleanArray) {
        if (!calculating) {
            buffer!!.put(Types.ARRAY)
            buffer!!.put(values.size.toByte())
        } else {
            sizeCounter += 2
        }

        values.forEach { writeElement(it) }
    }

    private fun writeElement(element: Any) {
        when (element) {
            is Long -> writeNumber(element)
            is String -> writeString(element)
            is ByteArray -> writeBuffer(element)
            is Serializable -> writeObject(element)
            is Boolean -> writeBoolean(element)
            is Array<*> -> writeObjectArray(element)
            is LongArray -> writeLongArray(element)
            is BooleanArray -> writeBoolArray(element)
        }
    }

    fun writeParam(parent: Serializable, key: String, element: Any) {
        val keyBytes = key.toByteArray()

        if (!calculating) {
            buffer!!.put(keyBytes.size.toByte())
            buffer!!.put(keyBytes)
        } else {
            sizeCounter += keyBytes.size + 1
        }

        parent.paramsCounter++
        writeElement(element)
    }

    fun serialize(element: Any, offset: Int): ByteBuffer {
        calculating = true
        writeElement(element)
        calculating = false

        buffer = ByteBuffer.allocateDirect(sizeCounter + offset)
        buffer!!.position(offset)
        sizeCounter = 0

        writeElement(element)

        val result = buffer
        result!!.flip()
        buffer = null

        return result
    }
}