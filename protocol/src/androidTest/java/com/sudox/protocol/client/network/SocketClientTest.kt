package com.sudox.protocol.client.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer

@RunWith(AndroidJUnit4::class)
class SocketClientTest : Assert() {

    private lateinit var server: TestSocketServer
    private lateinit var client: SocketClient
    private lateinit var callback: SocketCallbackMock

    @Before
    fun setUp() {
        server = TestSocketServer(4899)

        client = SocketClient("127.0.0.1", 4899)
        callback = SocketCallbackMock()
        client.callback(callback)
    }

    @After
    fun tearDown() {
        server.stopServer()
    }

    @Test
    fun testConnect_success() {
        server.startServer()
        client.connect()
        server.connectionSemaphore.acquire()
        callback.connectSemaphore.acquire()

        assertTrue(server.clientConnected)
        assertEquals(1, callback.connectedCalled)
    }

    @Test
    fun testConnect_error() {
        client.connect()
        callback.closedSemaphore.acquire()

        assertEquals(1, callback.closedCalled)
        assertTrue(callback.closedErrorLast)
    }

    @Test
    fun testClose_by_error() {
        server.startServer()
        client.connect()
        server.connectionSemaphore.acquire()
        callback.connectSemaphore.acquire()

        client.close(true)
        server.disconnectionSemaphore.acquire()
        callback.closedSemaphore.acquire()

        assertEquals(1, callback.closedCalled)
        assertTrue(callback.closedErrorLast)
        assertFalse(server.clientConnected)
    }

    @Test
    fun testClose_by_user() {
        server.startServer()
        client.connect()
        server.connectionSemaphore.acquire()
        callback.connectSemaphore.acquire()

        client.close(false)
        server.disconnectionSemaphore.acquire()
        callback.closedSemaphore.acquire()

        assertEquals(1, callback.closedCalled)
        assertFalse(callback.closedErrorLast)
        assertFalse(server.clientConnected)
    }

    @Test
    fun testClose_by_server() {
        server.startServer()
        client.connect()
        server.connectionSemaphore.acquire()
        callback.connectSemaphore.acquire()

        server.clientSocket!!.close()
        server.disconnectionSemaphore.acquire()
        callback.closedSemaphore.acquire()

        assertEquals(1, callback.closedCalled)
        assertTrue(callback.closedErrorLast)
        assertFalse(server.clientConnected)
    }

    @Test
    fun testReceiving() {
        server.startServer()
        client.connect()
        server.connectionSemaphore.acquire()
        callback.connectSemaphore.acquire()

        val bytes = "Hello World".toByteArray()
        server.send(bytes)
        callback.receiveSemaphore.acquire()

        val available = client.available()
        val received = client.read(available)
        assertEquals(1, callback.receivedCalled)
        assertEquals(bytes.size, available)
        assertArrayEquals(bytes, received)
    }

    @Test
    fun testReceiving_buffer() {
        server.startServer()
        client.connect()
        server.connectionSemaphore.acquire()
        callback.connectSemaphore.acquire()

        val bytes = "Hello World".toByteArray()
        server.send(bytes)
        callback.receiveSemaphore.acquire()

        val available = client.available()
        val buffer = ByteBuffer.allocateDirect(available)
        val received = client.read(buffer, available, 0)
        assertEquals(bytes.size, available)
        assertEquals(bytes.size, received)
        assertEquals(bytes.size, buffer.position())

        val bufferBytes = ByteArray(received)
        buffer.rewind()
        buffer.get(bufferBytes)
        assertArrayEquals(bytes, bufferBytes)
        assertEquals(1, callback.receivedCalled)
    }

    @Test
    fun testReceiving_buffer_positioning() {
        server.startServer()
        client.connect()
        server.connectionSemaphore.acquire()
        callback.connectSemaphore.acquire()

        val bytes = "Hello World".toByteArray()
        server.send(bytes)
        callback.receiveSemaphore.acquire()

        // Fragmented reading checking
        val buffer = ByteBuffer.allocateDirect(bytes.size)
        val firstReceived = client.read(buffer, 5, 0)
        assertEquals(5, firstReceived)
        assertEquals(5, buffer.position())

        val firstBytes = ByteArray(firstReceived)
        buffer.rewind()
        buffer.get(firstBytes)
        assertArrayEquals(bytes.copyOf(5), firstBytes)

        // Offset checking
        val secondReceived = client.read(buffer, 5, 5)
        assertEquals(5, secondReceived)
        assertEquals(10, buffer.position())

        val secondBytes = ByteArray(10)
        buffer.rewind()
        buffer.get(secondBytes)
        assertArrayEquals(bytes.copyOf(10), secondBytes)
        assertEquals(1, callback.receivedCalled)
    }

    @Test
    fun testSending_without_urgent_flag() {
        server.startServer()
        client.connect()
        server.connectionSemaphore.acquire()
        callback.connectSemaphore.acquire()

        val bytes = "Hello World".toByteArray()
        val buffer = ByteBuffer.allocateDirect(bytes.size)
        buffer.put(bytes)

        server.buffer = ByteBuffer.allocate(bytes.size)
        client.send(buffer, false)
        server.receivingSemaphore.acquire()

        assertArrayEquals(bytes, server.buffer!!.array())
    }
}