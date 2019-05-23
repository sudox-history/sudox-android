package com.sudox.protocol

import com.sudox.protocol.helpers.LENGTH_HEADER_SIZE_IN_BYTES
import com.sudox.protocol.helpers.toBytesBE
import com.sudox.sockets.SocketClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import java.nio.ByteBuffer

class ProtocolReaderTest : Assert() {

    private lateinit var socketClient: SocketClient
    private lateinit var reader: ProtocolReader

    @Before
    fun setUp() {
        socketClient = Mockito.mock(SocketClient::class.java)
        reader = ProtocolReader(socketClient)
    }

    @Test
    fun testPacketRead_fully_received() {
        val bytes = byteArrayOf(0, 0, 12.toByte(), 0, 0, 1.toByte(), 127, 0, 0, 2.toByte(), 1, 1)
        val lengthHeader = bytes.copyOf(LENGTH_HEADER_SIZE_IN_BYTES)
        val dataBytes = bytes.copyOfRange(lengthHeader.size, bytes.size)

        Mockito.`when`(socketClient.availableBytes()).thenReturn(bytes.size)
        Mockito.`when`(socketClient.readBytes(LENGTH_HEADER_SIZE_IN_BYTES)).thenReturn(lengthHeader)
        Mockito.`when`(socketClient.readToByteBuffer(any(), anyInt(), anyInt())).thenAnswer {
            val buffer = it.getArgument<ByteBuffer>(0)
            val count = it.getArgument<Int>(1)
            val offset = it.getArgument<Int>(2)

            buffer.position(offset)
            buffer.put(dataBytes)

            return@thenAnswer dataBytes.size
        }

        val readBuffer = reader.readPacketBytes()
        assertNotNull(readBuffer)

        val readBytes = ByteArray(readBuffer!!.limit())
        readBuffer.get(readBytes)
        assertArrayEquals(dataBytes, readBytes)
    }

    @Test
    fun testPacketRead_fully_received_but_length_is_negative() {
        val length = (-5).toBytesBE(3)
        val bytes = byteArrayOf(length[0], length[1], length[2], 0, 0, 1.toByte(), 127, 0, 0, 2.toByte(), 1, 1)
        val lengthHeader = bytes.copyOf(LENGTH_HEADER_SIZE_IN_BYTES)
        val dataBytes = bytes.copyOfRange(lengthHeader.size, bytes.size)

        Mockito.`when`(socketClient.availableBytes()).thenReturn(bytes.size)
        Mockito.`when`(socketClient.readBytes(LENGTH_HEADER_SIZE_IN_BYTES)).thenReturn(lengthHeader)
        Mockito.`when`(socketClient.readToByteBuffer(any(), anyInt(), anyInt())).thenAnswer {
            val buffer = it.getArgument<ByteBuffer>(0)
            val count = it.getArgument<Int>(1)
            val offset = it.getArgument<Int>(2)

            buffer.position(offset)
            buffer.put(dataBytes)

            return@thenAnswer dataBytes.size
        }

        val readBuffer = reader.readPacketBytes()
        assertNull(readBuffer)
    }

    @Test
    fun testPacketRead_partially_receiving() {
        val firstPart = byteArrayOf(0, 0)
        val secondPart = byteArrayOf(12)
        val lengthHeader = firstPart + secondPart
        val thirdPart = byteArrayOf(0, 0, 1, 127, 0, 0, 2, 1, 1)
        var availableBytes = 0

        Mockito.`when`(socketClient.availableBytes()).thenAnswer { availableBytes }

        // First part
        Mockito.`when`(socketClient.readBytes(LENGTH_HEADER_SIZE_IN_BYTES)).thenAnswer {
            return@thenAnswer firstPart
        }

        availableBytes = firstPart.size
        val firstBuffer = reader.readPacketBytes()
        assertNull(firstBuffer)
        Mockito.verify(socketClient, Mockito.never()).readBytes(anyInt())

        // Second part
        availableBytes = lengthHeader.size
        Mockito.`when`(socketClient.readBytes(LENGTH_HEADER_SIZE_IN_BYTES)).thenAnswer {
            return@thenAnswer lengthHeader
        }

        val secondBuffer = reader.readPacketBytes()
        assertNull(secondBuffer)
        Mockito.verify(socketClient).readBytes(LENGTH_HEADER_SIZE_IN_BYTES)

        // Third part
        availableBytes = lengthHeader.size
        Mockito.`when`(socketClient.readToByteBuffer(any(), anyInt(), anyInt())).thenAnswer {
            val buffer = it.getArgument<ByteBuffer>(0) ?: return@thenAnswer 0
            val count = it.getArgument<Int>(1)
            val offset = it.getArgument<Int>(2)

            buffer.position(offset)
            buffer.put(thirdPart)

            return@thenAnswer thirdPart.size
        }

        val thirdBuffer = reader.readPacketBytes()
        assertNotNull(thirdPart)

        val thirdBytes = ByteArray(thirdBuffer!!.limit())
        thirdBuffer.get(thirdBytes)

        assertArrayEquals(thirdPart, thirdBytes)
    }

    @Test
    fun testPacketRead_multiple_packets() {
        val firstPacket = byteArrayOf(0, 0, 12.toByte(), 0, 0, 1.toByte(), 127, 0, 0, 2.toByte(), 1, 1)
        val secondPacket = byteArrayOf(0, 0, 13.toByte(), 0, 0, 1.toByte(), 55, 0, 0, 3.toByte(), 1, 2, 3)

        val firstPacketLengthHeader = firstPacket.copyOf(LENGTH_HEADER_SIZE_IN_BYTES)
        val firstPacketDataBytes = firstPacket.copyOfRange(firstPacketLengthHeader.size, firstPacket.size)

        val secondPacketLengthHeader = secondPacket.copyOf(LENGTH_HEADER_SIZE_IN_BYTES)
        val secondPacketDataBytes = secondPacket.copyOfRange(secondPacketLengthHeader.size, secondPacket.size)

        Mockito.`when`(socketClient.availableBytes()).thenReturn(firstPacket.size)
        Mockito.`when`(socketClient.readBytes(LENGTH_HEADER_SIZE_IN_BYTES)).thenReturn(firstPacketLengthHeader)
        Mockito.`when`(socketClient.readToByteBuffer(any(), anyInt(), anyInt())).thenAnswer {
            val buffer = it.getArgument<ByteBuffer>(0) ?: return@thenAnswer 0
            val count = it.getArgument<Int>(1)
            val offset = it.getArgument<Int>(2)

            buffer.position(offset)
            buffer.put(firstPacketDataBytes)

            return@thenAnswer firstPacketDataBytes.size
        }

        val firstReadBuffer = reader.readPacketBytes()
        assertNotNull(firstReadBuffer)

        val firstReadBytes = ByteArray(firstReadBuffer!!.limit())
        firstReadBuffer.get(firstReadBytes)
        assertArrayEquals(firstPacketDataBytes, firstReadBytes)

        // Second packet
        Mockito.`when`(socketClient.availableBytes()).thenReturn(secondPacket.size)
        Mockito.`when`(socketClient.readBytes(LENGTH_HEADER_SIZE_IN_BYTES)).thenReturn(secondPacketLengthHeader)
        Mockito.`when`(socketClient.readToByteBuffer(any(), anyInt(), anyInt())).thenAnswer {
            val buffer = it.getArgument<ByteBuffer>(0) ?: return@thenAnswer 0
            val count = it.getArgument<Int>(1)
            val offset = it.getArgument<Int>(2)

            buffer.position(offset)
            buffer.put(secondPacketDataBytes)

            return@thenAnswer secondPacketDataBytes.size
        }

        val secondReadBuffer = reader.readPacketBytes()
        assertNotNull(secondReadBuffer)

        val secondReadBytes = ByteArray(secondReadBuffer!!.limit())
        secondReadBuffer.get(secondReadBytes)
        assertArrayEquals(secondPacketDataBytes, secondReadBytes)
    }
}