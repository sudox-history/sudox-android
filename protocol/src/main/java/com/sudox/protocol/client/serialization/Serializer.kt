package com.sudox.protocol.client.serialization

import com.sudox.protocol.client.serialization.helpers.calculateOctets
import com.sudox.protocol.client.serialization.helpers.writeIntLE
import com.sudox.protocol.client.serialization.helpers.writeLongLE
import java.nio.ByteBuffer

class Serializer {

    private var buffer: ByteBuffer? = null
    private var calculating: Boolean = true
    private var sizeCounter = 0

    private fun writeByte(type: Byte) {
        if (calculating) {
            sizeCounter++
            return
        }

        buffer!!.put(type)
    }

    private fun writeSize(value: Int, bytesCount: Int, offset: Int = 0) {
        if (calculating) {
            sizeCounter += bytesCount
            return
        }

        buffer!!.writeIntLE(value, bytesCount, offset)
    }

    private fun writeBoolean(value: Boolean) {
        writeByte(Types.BOOLEAN)

        if (!calculating) {
            buffer!!.put((if (value) 1 else 0).toByte())
            return
        }

        sizeCounter++
    }

    private fun writeNumber(value: Long) {
        val octets = value.calculateOctets()

        writeByte(Types.NUMBER)
        writeSize(octets, Types.NUMBER_HEADERS_LENGTH)

        if (calculating) {
            sizeCounter += octets
            return
        }

        buffer!!.writeLongLE(value, octets)
    }

    private fun writeString(value: String) {
        val bytes = value.toByteArray()
        val size = bytes.size

        writeByte(Types.STRING)
        writeSize(size, Types.STRING_HEADERS_LENGTH)

        if (calculating) {
            sizeCounter += size
            return
        }

        buffer!!.put(bytes)
    }

    private fun writeBuffer(value: ByteArray) {
        val size = value.size

        writeByte(Types.BUFFER)
        writeSize(size, Types.BUFFER_HEADERS_LENGTH)

        if (calculating) {
            sizeCounter += size
            return
        }

        buffer!!.put(value)
    }

    private fun writeObject(value: Serializable) {
        writeByte(Types.OBJECT)

        var countIndex = 0

        if (!calculating) {
            countIndex = buffer!!.position()
            buffer!!.position(countIndex + Types.OBJECT_HEADERS_LENGTH)
        }

        value.paramsCounter = 0
        value.serialize(this)
        writeSize(value.paramsCounter, Types.OBJECT_HEADERS_LENGTH, countIndex)
    }

    private fun writeObjectArray(values: Array<*>) {
        writeByte(Types.ARRAY)
        writeSize(values.size, Types.ARRAY_HEADERS_LENGTH)
        values.forEach {
            if (it != null) {
                writeElement(it)
            }
        }
    }

    private fun writeLongArray(values: LongArray) {
        writeByte(Types.ARRAY)
        writeSize(values.size, Types.ARRAY_HEADERS_LENGTH)
        values.forEach { writeElement(it) }
    }

    private fun writeBoolArray(values: BooleanArray) {
        writeByte(Types.ARRAY)
        writeSize(values.size, Types.ARRAY_HEADERS_LENGTH)
        values.forEach { writeElement(it) }
    }

    fun writeParam(parent: Serializable, key: String, element: Any) {
        val keyBytes = key.toByteArray()
        val keySize = keyBytes.size

        writeSize(keySize, Types.PARAM_HEADERS_LENGTH)

        if (!calculating) {
            buffer!!.put(keyBytes)
        } else {
            sizeCounter += keySize
        }

        writeElement(element)
        parent.paramsCounter++
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

    fun serialize(element: Any, offset: Int, allocateDirect: Boolean = false): ByteBuffer {
        calculating = true
        writeElement(element)
        calculating = false

        if (allocateDirect) {
            buffer = ByteBuffer.allocateDirect(sizeCounter + offset)
        } else {
            buffer = ByteBuffer.allocate(sizeCounter + offset)
        }

        buffer!!.position(offset)
        sizeCounter = 0

        writeElement(element)

        val result = buffer
        result!!.flip()
        buffer = null

        return result
    }
}