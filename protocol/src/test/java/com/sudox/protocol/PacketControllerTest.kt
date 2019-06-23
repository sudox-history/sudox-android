package com.sudox.protocol

import com.nhaarman.mockitokotlin2.any
import com.sudox.protocol.helpers.LENGTH_HEADER_SIZE_IN_BYTES
import com.sudox.protocol.helpers.serializePacket
import com.sudox.protocol.controllers.PacketController
import com.sudox.sockets.SocketClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import java.nio.ByteBuffer

class PacketControllerTest : Assert() {

    private lateinit var socketClient: SocketClient
    private lateinit var PacketController: PacketController

    @Before
    fun setUp() {
        socketClient = Mockito.mock(SocketClient::class.java)
        PacketController = PacketController(socketClient)
    }

    @Test
    fun testReading_packet_fully_delivered() {
        val buffer = serializePacket(arrayOf("hello".toByteArray()))
        val bytes = ByteArray(buffer.limit())
        buffer.get(bytes)

        var read = 0
        Mockito.`when`(socketClient.availableBytes()).thenAnswer { bytes.size - read }
        Mockito.`when`(socketClient.readBytes(anyInt())).thenAnswer {
            val count = it.arguments[0] as Int
            val result = bytes.copyOfRange(read, read + count)
            read += count
            return@thenAnswer result
        }

        Mockito.`when`(socketClient.readToByteBuffer(any(), anyInt(), anyInt())).thenAnswer {
            val buf = it.arguments[0] as ByteBuffer
            val count = it.arguments[1] as Int
            val offset = it.arguments[2] as Int
            val result = bytes.copyOfRange(read, read + count)
            val bufPos = buf.position()
            buf.put(result, offset, result.size)
            buf.position(bufPos + result.size)
            return@thenAnswer result.size
        }

        val result = PacketController.readPacket()
        assertNotNull(result)

        val resultBytes = ByteArray(result!!.limit())
        val validBytes = bytes.copyOfRange(LENGTH_HEADER_SIZE_IN_BYTES, bytes.size)
        result.get(resultBytes)
        assertArrayEquals(validBytes, resultBytes)
    }

    @Test
    fun testReading_packet_fragmented() {

    }

    @Test
    fun testReading_length_fragmented() {

    }
}