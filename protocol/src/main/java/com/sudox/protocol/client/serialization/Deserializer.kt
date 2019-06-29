package com.sudox.protocol.client.serialization

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

        val length = internalBuffer!!.get().toInt()
        var value = 0L

        if (internalBuffer!!.remaining() < length) {
            return 0
        }

        for (i in 0 until length) {
            val byte = internalBuffer!!.get().toLong()

            value = value or if (i < length - 1) {
                (((byte + 256) and 0xFF) shl (i * 8))
            } else {
                (byte shl (8 * i))
            }
        }

        return value
    }

    private fun readString(): String? {
        if (internalBuffer!!.remaining() < Types.STRING_HEADERS_LENGTH) {
            return null
        }

        val firstLengthByte = internalBuffer!!.get().toInt() and 0xFF
        val secondLengthByte = (internalBuffer!!.get().toInt() and 0xFF) shl 8
        val length = firstLengthByte or secondLengthByte

        if (internalBuffer!!.remaining() < length) {
            return null
        }

        val bytes = ByteArray(length).apply {
            internalBuffer!!.get(this)
        }

        return String(bytes)
    }

    private fun readBuffer(): ByteArray? {
        if (internalBuffer!!.remaining() < Types.BUFFER_HEADERS_LENGTH) {
            return null
        }

        val firstLengthByte = internalBuffer!!.get().toInt() and 0xFF
        val secondLengthByte = (internalBuffer!!.get().toInt() and 0xFF) shl 8
        val length = firstLengthByte or secondLengthByte

        if (internalBuffer!!.remaining() < length) {
            return null
        }

        return ByteArray(length).apply {
            internalBuffer!!.get(this)
        }
    }

    private fun readArrayInternal(): Array<Any?>? {
        if (internalBuffer!!.remaining() < Types.ARRAY_HEADERS_LENGTH) {
            return null
        }

        val size = internalBuffer!!.get().toInt()
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

        val paramsCount = internalBuffer!!.get().toInt()
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
        if (!internalBuffer!!.hasRemaining()) {
            return null
        }

        val keySize = internalBuffer!!.get().toInt()

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