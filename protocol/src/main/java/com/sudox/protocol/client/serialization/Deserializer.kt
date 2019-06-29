package com.sudox.protocol.client.serialization

import com.sudox.protocol.client.serialization.helpers.readLongLE
import com.sudox.protocol.client.serialization.helpers.readUIntLE
import java.nio.ByteBuffer
import kotlin.reflect.KClass

class Deserializer {

    private var internalBuffer: ByteBuffer? = null
    private var internalParams: LinkedHashMap<String, Any>? = null

    private fun readBoolean(): Boolean {
        if (!internalBuffer!!.hasRemaining()) {
            return false
        }

        return internalBuffer!!.get() == 1.toByte()
    }

    private fun readNumber(): Long {
        if (internalBuffer!!.remaining() < Types.NUMBER_HEADERS_LENGTH) {
            return 0
        }

        val octets = internalBuffer!!.readUIntLE(Types.NUMBER_HEADERS_LENGTH)

        if (internalBuffer!!.remaining() < octets) {
            return 0
        }

        return internalBuffer!!.readLongLE(octets)
    }

    private fun readString(): String? {
        if (internalBuffer!!.remaining() < Types.STRING_HEADERS_LENGTH) {
            return null
        }

        val size = internalBuffer!!.readUIntLE(Types.STRING_HEADERS_LENGTH)

        if (internalBuffer!!.remaining() < size) {
            return null
        }

        return String(ByteArray(size).apply {
            internalBuffer!!.get(this)
        })
    }

    private fun readBuffer(): ByteArray? {
        if (internalBuffer!!.remaining() < Types.BUFFER_HEADERS_LENGTH) {
            return null
        }

        val size = internalBuffer!!.readUIntLE(Types.BUFFER_HEADERS_LENGTH)

        if (internalBuffer!!.remaining() < size) {
            return null
        }

        return ByteArray(size).apply {
            internalBuffer!!.get(this)
        }
    }

    private fun readArrayInternal(): Array<Any?>? {
        if (internalBuffer!!.remaining() < Types.ARRAY_HEADERS_LENGTH) {
            return null
        }

        val size = internalBuffer!!.readUIntLE(Types.ARRAY_HEADERS_LENGTH)
        val array = arrayOfNulls<Any?>(size)

        for (i in 0 until size) {
            array[i] = readElement() ?: return null
        }

        return array
    }

    private fun readObjectInternal(): LinkedHashMap<String, Any>? {
        if (internalBuffer!!.remaining() < Types.OBJECT_HEADERS_LENGTH) {
            return null
        }

        val paramsCount = internalBuffer!!.readUIntLE(Types.OBJECT_HEADERS_LENGTH)
        val params = LinkedHashMap<String, Any>(paramsCount)

        if (internalParams == null) {
            internalParams = params
        }

        for (i in 1..paramsCount) {
            params.plusAssign(readParam() ?: return null)
        }

        return params
    }

    private fun readParam(): Pair<String, Any>? {
        if (internalBuffer!!.remaining() < Types.PARAM_HEADERS_LENGTH) {
            return null
        }

        val keySize = internalBuffer!!.readUIntLE(Types.PARAM_HEADERS_LENGTH)

        if (internalBuffer!!.remaining() < keySize) {
            return null
        }

        val keyBytes = ByteArray(keySize).apply { internalBuffer!!.get(this) }
        val key = String(keyBytes)
        val element = readElement() ?: return null

        return Pair(key, element)
    }

    private fun readElement(): Any? {
        if (!internalBuffer!!.hasRemaining()) {
            return null
        }

        return when (internalBuffer!!.get()) {
            Types.BOOLEAN -> readBoolean()
            Types.NUMBER -> readNumber()
            Types.STRING -> readString()
            Types.BUFFER -> readBuffer()
            Types.ARRAY -> readArrayInternal()
            Types.OBJECT -> readObjectInternal()
            else -> null
        }
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    fun deserialize(buffer: ByteBuffer, objectClass: KClass<out Serializable>? = null): Any? {
        internalBuffer = buffer
        val result = readElement()
        internalBuffer = null
        internalParams = null

        if (result == null) {
            return null
        }

        return if (objectClass == null) {
            result
        } else if (result is LinkedHashMap<*, *>) {
            buildObject(result, objectClass)
        } else if (result is Array<*>) {
            buildArray(result, objectClass)
        } else {
            result
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun buildObject(params: LinkedHashMap<*, *>, clazz: KClass<out Serializable>): Serializable {
        return clazz.java.newInstance().apply { deserialize(params as LinkedHashMap<String, Any>) }
    }

    private fun buildArray(array: Array<*>, clazz: KClass<out Serializable>): Array<Any?> {
        return Array(array.size) {
            val element = array[it]

            if (element is LinkedHashMap<*, *>) {
                buildObject(element, clazz)
            } else {
                array[it]
            }
        }
    }
}