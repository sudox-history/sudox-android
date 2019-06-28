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
        var size = 1
        var pow = 256

        if (!calculating) {
            buffer!!.put(Types.NUMBER)
            sizeIndex = buffer!!.position()
            buffer!!.position(sizeIndex + 1)
        }

        while (true) {
            if (!calculating) {
                buffer!!.put((value ushr ((size - 1) * 8)).toByte())
            }

            val max = pow / 2 - 1
            val min = -max + 1
            pow *= 256

            if (value in min..max || size >= 8) {
                break
            }

            size++
        }

        if (!calculating) {
            writeSizeLE(size, Types.NUMBER_HEADERS_LENGTH, sizeIndex)
        } else {
            sizeCounter += size + Types.NUMBER_HEADERS_LENGTH + 1
        }
    }

    private fun writeSizeLE(value: Int, bytesCount: Int, offset: Int = 0) {
        for (i in 1..bytesCount) {
            if (offset > 0) {
                buffer!!.put(offset + i - 1, (value ushr ((i - 1) * 8)).toByte())
            } else {
                buffer!!.put((value ushr ((i - 1) * 8)).toByte())
            }
        }
    }

    private fun writeString(value: String) {
        val bytes = value.toByteArray()
        val size = bytes.size

        if (!calculating) {
            buffer!!.put(Types.STRING)
            writeSizeLE(size, Types.STRING_HEADERS_LENGTH)
            buffer!!.put(bytes)
        } else {
            sizeCounter += size + Types.STRING_HEADERS_LENGTH + 1
        }
    }

    private fun writeBuffer(value: ByteArray) {
        val size = value.size

        if (!calculating) {
            buffer!!.put(Types.BUFFER)
            writeSizeLE(size, Types.BUFFER_HEADERS_LENGTH)
            buffer!!.put(value)
        } else {
            sizeCounter += size + Types.BUFFER_HEADERS_LENGTH + 1
        }
    }

    private fun writeObject(value: Serializable) {
        var sizeIndex = 0

        if (!calculating) {
            buffer!!.put(Types.OBJECT)
            sizeIndex = buffer!!.position()
            buffer!!.position(sizeIndex + Types.OBJECT_HEADERS_LENGTH)
        }

        value.paramsCounter = 0
        value.serialize(this)

        if (!calculating) {
            writeSizeLE(value.paramsCounter, Types.OBJECT_HEADERS_LENGTH, sizeIndex)
        } else {
            sizeCounter += Types.OBJECT_HEADERS_LENGTH + 1
        }
    }

    private fun writeObjectArray(values: Array<*>) {
        if (!calculating) {
            buffer!!.put(Types.ARRAY)
            writeSizeLE(values.size, Types.ARRAY_HEADERS_LENGTH)
        } else {
            sizeCounter += Types.ARRAY_HEADERS_LENGTH + 1
        }

        values.forEach { writeElement(it!!) }
    }

    private fun writeLongArray(values: LongArray) {
        if (!calculating) {
            buffer!!.put(Types.ARRAY)
            writeSizeLE(values.size, Types.ARRAY_HEADERS_LENGTH)
        } else {
            sizeCounter += Types.ARRAY_HEADERS_LENGTH + 1
        }

        values.forEach { writeElement(it) }
    }

    private fun writeBoolArray(values: BooleanArray) {
        if (!calculating) {
            buffer!!.put(Types.ARRAY)
            writeSizeLE(values.size, Types.ARRAY_HEADERS_LENGTH)
        } else {
            sizeCounter += Types.ARRAY_HEADERS_LENGTH + 1
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
            writeSizeLE(keyBytes.size, Types.PARAM_HEADERS_LENGTH)
            buffer!!.put(keyBytes)
        } else {
            sizeCounter += Types.PARAM_HEADERS_LENGTH + keyBytes.size
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