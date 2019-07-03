package com.sudox.protocol.client.controllers

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.eq
import com.sudox.protocol.client.network.SocketClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.nio.ByteBuffer

@ExperimentalUnsignedTypes
@PrepareForTest(SocketClient::class)
@RunWith(PowerMockRunner::class)
class PacketControllerTest : Assert() {

    private lateinit var socketClient: SocketClient
    private lateinit var packetController: PacketController

    private lateinit var bytes: ByteArray
    private var available: Int = 0

    @Before
    fun setUp() {
        socketClient = PowerMockito.mock(SocketClient::class.java)
        packetController = PacketController(socketClient)
    }

    @Test
    fun testPacketNotFragmented() {
        // ["tst", <Buffer 32 02 1e 03 00 74 73 74 28 03 00 01 02 03>]
        val bytes = ubyteArrayOf(14u, 0u, 0x32u, 0x02u, 0x1eu, 0x03u, 0x00u, 0x74u, 0x73u, 0x74u,
                0x28u, 0x03u, 0x00u, 0x01u, 0x02u, 0x03u).toByteArray()

        configureSocketClient(bytes, true)

        val result = packetController.readPacket()
        assertTrue(result is Array<*>)

        val array = result as Array<*>
        assertEquals(2, array.size)
        assertTrue(result[0] is String)
        assertTrue(result[1] is ByteArray)

        assertNull(packetController.readPacket())
    }

    @Test
    fun testPacketFragmented() {
        // ["tst", <Buffer 32 02 1e 03 00 74 73 74 28 03 00 01 02 03>]
        val bytes = ubyteArrayOf(14u, 0u, 0x32u, 0x02u, 0x1eu, 0x03u, 0x00u, 0x74u, 0x73u, 0x74u,
                0x28u, 0x03u, 0x00u, 0x01u, 0x02u, 0x03u).toByteArray()

        val firstFragment = bytes.copyOfRange(0, 2)
        val secondFragment = bytes.copyOfRange(2, 4)
        val thirdFragment = bytes.copyOfRange(4, bytes.size)

        configureSocketClient(firstFragment, true)
        assertNull(packetController.readPacket())

        configureSocketClient(secondFragment, false)
        assertNull(packetController.readPacket())

        configureSocketClient(thirdFragment, false)
        val result = packetController.readPacket()
        assertTrue(result is Array<*>)

        val array = result as Array<*>
        assertEquals(2, array.size)
        assertTrue(result[0] is String)
        assertTrue(result[1] is ByteArray)

        assertNull(packetController.readPacket())
    }

    @Test
    fun testLengthFragmented() {
        // ["tst", <Buffer 32 02 1e 03 00 74 73 74 28 03 00 01 02 03>]
        val bytes = ubyteArrayOf(14u, 0u, 0x32u, 0x02u, 0x1eu, 0x03u, 0x00u, 0x74u, 0x73u, 0x74u,
                0x28u, 0x03u, 0x00u, 0x01u, 0x02u, 0x03u).toByteArray()

        val firstFragment = bytes.copyOfRange(0, 1)
        val secondFragment = bytes.copyOfRange(1, bytes.size)

        configureSocketClient(firstFragment, true)
        assertNull(packetController.readPacket())

        configureSocketClient(firstFragment + secondFragment, false)
        val result = packetController.readPacket()
        assertTrue(result is Array<*>)

        val array = result as Array<*>
        assertEquals(2, array.size)
        assertTrue(result[0] is String)
        assertTrue(result[1] is ByteArray)

        assertNull(packetController.readPacket())
    }

    @Test
    fun testPacketSending() {
        packetController.sendPacket(arrayOf("tst", byteArrayOf(1, 2, 3)), true)

        Mockito.verify(socketClient).send(argThat {
            rewind()

            val bytes = ByteArray(limit()).apply { get(this) }
            val valid = ubyteArrayOf(14u, 0u, 0x32u, 0x02u, 0x1eu, 0x03u, 0x00u, 0x74u, 0x73u,
                    0x74u, 0x28u, 0x03u, 0x00u, 0x01u, 0x02u, 0x03u).toByteArray()

            bytes.contentEquals(valid)
        }, eq(true))
    }

    private fun configureSocketClient(received: ByteArray, mockMethod: Boolean) {
        bytes = received
        available = received.size

        if (mockMethod) {
            Mockito.`when`(socketClient.available()).thenAnswer { available }
            Mockito.`when`(socketClient.read(anyInt())).thenAnswer {
                val count = it.getArgument<Int>(0)

                if (count > socketClient.available()) {
                    return@thenAnswer null
                }

                val read = bytes.copyOfRange(bytes.size - available, bytes.size - available + count)
                available -= read.size
                return@thenAnswer read
            }

            Mockito.`when`(socketClient.read(any(), anyInt(), anyInt())).thenAnswer {
                val buffer = it.getArgument<ByteBuffer>(0)
                val count = it.getArgument<Int>(1)
                val offset = it.getArgument<Int>(2)
                val read = socketClient.read(count) ?: return@thenAnswer -1

                buffer.position(offset)
                buffer.put(read)

                return@thenAnswer read.size
            }
        }
    }
}