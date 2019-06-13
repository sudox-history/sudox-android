package com.sudox.sockets

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.nio.ByteBuffer
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class SocketClientTest {

    private var port: Int = 0
    private lateinit var client: SocketClient
    private lateinit var serverSocket: ServerSocket

    @Before
    fun setUp() {
        port = Random.nextInt(1000, 32767)
        client = SocketClient("localhost", port.toShort())
        serverSocket = ServerSocket()
    }

    @After
    fun tearDown() {
        stopServer()
    }

    fun startServer() {
        serverSocket.bind(InetSocketAddress(port))
    }

    fun stopServer() {
        client.close(false)
        serverSocket.close()
    }

    @Test
    fun testConnect_fail_contact_with_server() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)

        client.callback(callback)
        client.connect()

        Thread.sleep(500) // Waiting callback ...

        Mockito.verify(callback).socketClosed(true)
        assertFalse(client.opened())
        assertEquals(-1, client.availableBytes())
        assertNull(client.readBytes(1024))
        assertEquals(-1, client.readToByteBuffer(ByteBuffer.allocateDirect(10), 10, 0))
    }

    @Test
    fun testConnect_success() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)
        val connectSemaphore = Semaphore(0)
        var isConnectedToServer = false

        startServer()
        thread {
            serverSocket.accept()
            isConnectedToServer = true
            connectSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        client.callback(callback)
        client.connect()

        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        Thread.sleep(500) // Waiting callback ...
        Mockito.verify(callback).socketConnected()
        assertTrue(isConnectedToServer)
        assertTrue(client.opened())
        assertEquals(0, client.availableBytes())
        assertNull(client.readBytes(1024))
        assertEquals(-1, client.readToByteBuffer(ByteBuffer.allocateDirect(10), 10, 0))
    }

    @Test
    fun testClose_not_connected() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)

        client.callback(callback)
        client.close(false)
        client.close(true)

        Mockito.verify(callback, Mockito.never()).socketClosed(Mockito.anyBoolean())
        assertFalse(client.opened())
        assertEquals(-1, client.availableBytes())
        assertNull(client.readBytes(1024))
        assertEquals(-1, client.readToByteBuffer(ByteBuffer.allocateDirect(10), 10, 0))
    }

    @Test
    fun testClose_by_error() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)
        val connectSemaphore = Semaphore(0)
        val disconnectSemaphore = Semaphore(0)

        startServer()
        thread {
            val socket = serverSocket.accept()
            connectSemaphore.release()

            socket.getInputStream().read()
            disconnectSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        client.callback(callback)
        client.connect()

        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)
        client.close(true)
        disconnectSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        Mockito.verify(callback).socketClosed(true)
        assertFalse(client.opened())
        assertEquals(-1, client.availableBytes())
        assertNull(client.readBytes(1024))
        assertEquals(-1, client.readToByteBuffer(ByteBuffer.allocateDirect(10), 10, 0))
    }

    @Test
    fun testClose_by_user() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)
        val connectSemaphore = Semaphore(0)
        val disconnectSemaphore = Semaphore(0)

        startServer()
        thread {
            val socket = serverSocket.accept()
            connectSemaphore.release()

            socket.getInputStream().read()
            disconnectSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        client.callback(callback)
        client.connect()
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        client.close(false)
        disconnectSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        Mockito.verify(callback).socketClosed(false)
        assertFalse(client.opened())
        assertEquals(-1, client.availableBytes())
        assertNull(client.readBytes(1024))
        assertEquals(-1, client.readToByteBuffer(ByteBuffer.allocateDirect(10), 10, 0))
    }

    @Test
    fun testSend_before_connection() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)
        val connectSemaphore = Semaphore(0)
        val readSemaphore = Semaphore(0)
        val read = ByteArray(128)

        client.callback(callback)
        client.send(byteArrayOf(123))

        startServer()
        thread {
            val socket = serverSocket.accept()
            connectSemaphore.release()

            socket.getInputStream().read(read)
            readSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        client.connect()
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        readSemaphore.tryAcquire(5, TimeUnit.SECONDS)
        assertEquals(0.toByte(), read[0])
    }

    @Test
    fun testSend_with_connection() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)
        val connectSemaphore = Semaphore(0)
        val readSemaphore = Semaphore(0)
        val read = ByteArray(128)
        val message = "Hello, World!"
                .toByteArray()
                .copyOf(128)

        client.callback(callback)

        startServer()
        thread {
            val socket = serverSocket.accept()
            connectSemaphore.release()

            socket.getInputStream().read(read)
            readSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        client.connect()
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        client.send(message)
        readSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        assertArrayEquals(message, read)
    }

    @Test
    fun testSendBuffer_direct() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)
        val connectSemaphore = Semaphore(0)
        val readSemaphore = Semaphore(0)
        var read = ByteArray(128)
        var readCount = 0
        val message = "Hello, World!".toByteArray()

        val buffer = ByteBuffer.allocateDirect(message.size)
        buffer.put(message)

        client.callback(callback)

        startServer()
        thread {
            val socket = serverSocket.accept()
            connectSemaphore.release()

            readCount = socket.getInputStream().read(read)
            readSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        client.connect()
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        client.sendBuffer(buffer)
        readSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        read = read.copyOf(readCount)
        assertArrayEquals(message, read)
    }

    @Test
    fun testSendBuffer_heap() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)
        val connectSemaphore = Semaphore(0)
        val readSemaphore = Semaphore(0)
        var read = ByteArray(128)
        var readCount = 0
        val message = "Hello, World!".toByteArray()

        val buffer = ByteBuffer.wrap(message)

        client.callback(callback)

        startServer()
        thread {
            val socket = serverSocket.accept()
            connectSemaphore.release()

            readCount = socket.getInputStream().read(read)
            readSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        client.connect()
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        client.sendBuffer(buffer)
        readSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        read = read.copyOf(readCount)
        assertArrayEquals(message, read)
    }

    @Test
    fun testReceive() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)
        val connectSemaphore = Semaphore(0)
        val writeSemaphore = Semaphore(0)
        val readSemaphore = Semaphore(0)
        val message = "Hello, World!".toByteArray()

        startServer()
        thread {
            val socket = serverSocket.accept()
            connectSemaphore.release()

            writeSemaphore.tryAcquire(5, TimeUnit.SECONDS)
            socket.getOutputStream().write(message)
            readSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        client.callback(callback)
        client.connect()
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        writeSemaphore.release()

        readSemaphore.tryAcquire(5, TimeUnit.SECONDS)
        Thread.sleep(500) // Waiting callback ...
        Mockito.verify(callback).socketReceive()

        val availableBefore = client.availableBytes()
        val received = client.readBytes(2048)
        val availableAfter = client.availableBytes()

        assertArrayEquals(message, received)
        assertEquals(message.size, availableBefore)
        assertEquals(0, availableAfter)
    }

    @Test
    fun testReceiveBuffer() {
        val callback = Mockito.mock(SocketClient.ClientCallback::class.java)
        val connectSemaphore = Semaphore(0)
        val writeSemaphore = Semaphore(0)
        val readSemaphore = Semaphore(0)
        val message = "Hello, World!".toByteArray()

        startServer()
        thread {
            val socket = serverSocket.accept()
            connectSemaphore.release()

            writeSemaphore.tryAcquire(5, TimeUnit.SECONDS)
            socket.getOutputStream().write(message)
            readSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        client.callback(callback)
        client.connect()
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        writeSemaphore.release()

        readSemaphore.tryAcquire(5, TimeUnit.SECONDS)
        Thread.sleep(500) // Waiting callback ...
        Mockito.verify(callback).socketReceive()

        val buffer = ByteBuffer.allocateDirect(message.size)
        var availableBefore = client.availableBytes()
        var receivedCount = client.readToByteBuffer(buffer, 5, 0)
        var availableAfter = client.availableBytes()

        assertEquals(5, receivedCount)
        assertEquals(message.size, availableBefore)
        assertEquals(message.size - 5, availableAfter)

        // Second part
        availableBefore = client.availableBytes()
        receivedCount = client.readToByteBuffer(buffer, availableBefore, receivedCount)
        availableAfter = client.availableBytes()

        assertEquals(availableBefore, receivedCount)
        assertEquals(0, availableAfter)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testReceiveBuffer_heap() {
        val buffer = ByteBuffer.allocate(128)

        client.readToByteBuffer(buffer, 5, 0)
    }
}