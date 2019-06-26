package com.sudox.protocol.client.serialization

import java.nio.ByteBuffer
import kotlin.reflect.KClass

class Deserializer {

    var buffer: ByteBuffer? = null
    var params: LinkedHashMap<String, Any>? = null

    private fun readBoolean(defValue: Boolean = false): Boolean {
        if (!buffer!!.hasRemaining()) {
            return defValue
        }

        return buffer!!.get() == 1.toByte()
    }

    private fun readNumber(defValue: Long = 0): Long {
        if (buffer!!.remaining() < Types.NUMBER_HEADERS_LENGTH) {
            return defValue
        }

        val length = buffer!!.get()
        var value = 0L

        if (buffer!!.remaining() < length) {
            return defValue
        }

        for (i in 0 until length) {
            val byte = buffer!!.get().toLong()

            value = value or if (i > 0) {
                (byte shr (i * 8))
            } else {
                (byte and 0xFF)
            }
        }

        return value
    }

    private fun readString(): String? {
        if (buffer!!.remaining() < Types.STRING_HEADERS_LENGTH) {
            return null
        }

        val firstLengthByte = buffer!!.get().toInt()
        val secondLengthByte = buffer!!.get().toInt() shl 8
        val length = firstLengthByte or secondLengthByte

        if (buffer!!.remaining() < length) {
            return null
        }

        val bytes = ByteArray(length).apply {
            buffer!!.get(this)
        }

        return String(bytes)
    }

    private fun readBuffer(): ByteArray? {
        if (buffer!!.remaining() < Types.BUFFER_HEADERS_LENGTH) {
            return null
        }

        val firstLengthByte = buffer!!.get().toInt()
        val secondLengthByte = buffer!!.get().toInt() shl 8
        val length = firstLengthByte or secondLengthByte

        if (buffer!!.remaining() < length) {
            return null
        }

        return ByteArray(length).apply {
            buffer!!.get(this)
        }
    }

    private fun readObjectInternal(): LinkedHashMap<String, Any>? {
        if (buffer!!.remaining() < Types.OBJECT_HEADERS_LENGTH) {
            return null
        }

        val paramsCount = buffer!!.get().toInt()
        val params = LinkedHashMap<String, Any>(paramsCount)

        if (this.params == null) {
            this.params = params
        }

        for (i in 1..paramsCount) {
            params.plusAssign(readParam()!!)
        }

        return params
    }

    private fun readArrayInternal(): Array<Any?>? {
        if (buffer!!.remaining() < Types.ARRAY_HEADERS_LENGTH) {
            return null
        }

        val size = buffer!!.get().toInt()
        val array = arrayOfNulls<Any?>(size)

        for (i in 0 until size) {
            array[i] = readElement()
        }

        return array
    }

    private fun readParam(): Pair<String, Any>? {
        if (!buffer!!.hasRemaining()) {
            return null
        }

        val keySize = buffer!!.get().toInt()

        if (buffer!!.remaining() < keySize) {
            return null
        }

        val keyBytes = ByteArray(keySize).apply { buffer!!.get(this) }
        val key = String(keyBytes)
        val element = readElement() ?: return null

        return Pair(key, element)
    }

    fun readArray(): Array<Any?>? {
        if (!buffer!!.hasRemaining() || buffer!!.get() != Types.ARRAY) {
            return null
        }

        return readArrayInternal()
    }

    fun <T : Serializable> readObject(clazz: KClass<T>): T? {
        if (!buffer!!.hasRemaining() || buffer!!.get() != Types.OBJECT) {
            return null
        }

        if (readObjectInternal() == null) {
            return null
        }

        return clazz.java.newInstance().apply {
            deserialize(params!!)
            params = null
        }
    }

    private fun readElement(): Any? {
        return when (buffer!!.get()) {
            Types.BOOLEAN -> readBoolean()
            Types.NUMBER -> readNumber()
            Types.STRING -> readString()
            Types.BUFFER -> readBuffer()
            Types.ARRAY -> readArrayInternal()
            Types.OBJECT -> readObjectInternal()
            else -> null
        }
    }

    fun reset() {
        buffer = null
        params = null
    }
}