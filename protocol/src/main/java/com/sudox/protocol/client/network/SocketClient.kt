package com.sudox.protocol.client.network

import java.nio.ByteBuffer

class SocketClient(host: String, port: Short) {

    private var pointer: Long = 0

    init {
        loadLibrary()
        pointer = createNativeInstance(host, port)
    }

    fun connect() = connect(pointer)
    fun close(error: Boolean) = close(pointer, error)
    fun callback(callback: SocketCallback) = callback(pointer, callback)
    fun available() = available(pointer)
    fun send(buffer: ByteBuffer, urgent: Boolean) = send(pointer, buffer, buffer.limit(), urgent)
    fun read(count: Int): ByteArray = read0(pointer, count)

    fun read(buffer: ByteBuffer, count: Int, offset: Int): Int {
        val read = read1(pointer, buffer, count, offset)

        if (read > 0) {
            buffer.position(buffer.position() + read)
        }

        return read
    }
}

internal var isLibraryLoaded: Boolean = false

internal fun loadLibrary() {
    if (!isLibraryLoaded) {
        System.loadLibrary("ssockets")
    }
}

internal external fun createNativeInstance(host: String, port: Short): Long
internal external fun connect(pointer: Long)
internal external fun close(pointer: Long, error: Boolean)
internal external fun callback(pointer: Long, callback: SocketCallback)
internal external fun available(pointer: Long): Int
internal external fun send(pointer: Long, buffer: ByteBuffer, length: Int, urgent: Boolean)
internal external fun read0(pointer: Long, count: Int): ByteArray
internal external fun read1(pointer: Long, buffer: ByteBuffer, count: Int, offset: Int): Int