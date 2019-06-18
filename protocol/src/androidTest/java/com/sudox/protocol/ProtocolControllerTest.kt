package com.sudox.protocol

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.any
import com.sudox.protocol.controllers.PING_PACKET_NAME
import com.sudox.protocol.controllers.PING_SEND_INTERVAL_IN_MILLIS
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ProtocolControllerTest : Assert() {
    private var port: Int = 0
    private lateinit var client: ProtocolClient
    private lateinit var controller: ProtocolController
    private lateinit var serverSocket: ServerSocket
    private lateinit var callback: ProtocolCallback

    @Before
    fun setUp() {
        port = Random.nextInt(9000, 10000)
        callback = Mockito.mock(ProtocolCallback::class.java)
        client = ProtocolClient("127.0.0.1", port.toShort(), callback)
        controller = ProtocolController(client)
        serverSocket = ServerSocket()
    }

    @After
    fun tearDown() {
        stopServer()
        controller.interrupt()
    }

    fun startServer() {
        serverSocket.bind(InetSocketAddress(port))
    }

    fun stopServer() {
        serverSocket.close()
    }

    @Test
    fun testSuccessfullyConnection() {
        var isConnectedToServer = false

        startServer()
        thread {
            serverSocket.accept()
            isConnectedToServer = true
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        controller.start()
        controller.join(500)

        assertTrue(isConnectedToServer)
        assertFalse(controller.connectionAttemptFailed)
    }

    @Test
    fun testCloseConnection() {
        val connectSemaphore = Semaphore(0)
        var connected = false

        startServer()
        thread {
            serverSocket.accept()
            connectSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        controller.start()
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)
        controller.closeConnection()

        thread {
            serverSocket.accept()
            connected = true
            connectSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)
        assertFalse(connected)
    }

    @Test
    fun testPing() {
        val connectSemaphore = Semaphore(0)

        startServer()
        thread {
            serverSocket.accept()
            connectSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        controller.start()

        // Because onEnded() never calling without session
        controller.messagesController.startSession("Fake key!".toByteArray())
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)
        Thread.sleep(10000)

        Mockito.verify(callback).onEnded()
    }

    @Test
    fun testPing_normal() {
        val connectSemaphore = Semaphore(0)
        val pingMessage = byteArrayOf(0x00, 0x00, 0x09, 0x00, 0x00, 0x03) + PING_PACKET_NAME
        val pingReceivedMessage = ByteArray(pingMessage.size)

        startServer()
        thread {
            val socket = serverSocket.accept()
            socket.tcpNoDelay = true
            socket.getOutputStream().write(pingMessage)
            connectSemaphore.release()
            Thread.sleep(2000)
            socket.getInputStream().read(pingReceivedMessage)

            for (i in 0 until 10) {
                Thread.sleep(PING_SEND_INTERVAL_IN_MILLIS)
                socket.getOutputStream().write(pingMessage)
            }
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        controller.start()

        // Because onEnded() never calling without session
        controller.messagesController.startSession("Fake key!".toByteArray())
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)
        Thread.sleep(10000)

        Mockito.verify(callback, Mockito.never()).onEnded()
        assertArrayEquals(pingMessage, pingReceivedMessage)
    }

    @Test
    fun testRestartConnection() {
        val connectSemaphore = Semaphore(0)
        var connected = false

        startServer()
        thread {
            serverSocket.accept()
            connectSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        controller.start()
        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)
        controller.restartConnection()

        thread {
            serverSocket.accept()
            connected = true
            connectSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        connectSemaphore.tryAcquire(5, TimeUnit.SECONDS)
        assertTrue(connected)
    }

    @Test
    fun testReconnectionBeforeError() {
        var isConnectedToServer = false

        controller.start()
        controller.join(500)

        assertTrue(controller.connectionAttemptFailed)
        Mockito.verify(callback, Mockito.never()).onMessage(any())
        Mockito.verify(callback, Mockito.never()).onEnded()
        Mockito.verify(callback, Mockito.never()).onStarted()

        startServer()
        thread {
            serverSocket.accept()
            isConnectedToServer = true
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        controller.join(RECONNECT_ATTEMPTS_INTERVAL_IN_MILLIS)
        assertTrue(isConnectedToServer)
        assertFalse(controller.messagesController.isSessionStarted())
        assertFalse(controller.connectionAttemptFailed)
    }

    @Test
    fun testConnectionDropping_without_session() {
        val disconnectionSemaphore = Semaphore(0)

        startServer()
        thread {
            val socket = serverSocket.accept()
            socket.close()
            disconnectionSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        controller.start()
        controller.join(500)

        stopServer() // Preventing reconnection
        disconnectionSemaphore.acquire()
        controller.join(500)
        controller.interrupt()

        assertFalse(controller.messagesController.isSessionStarted())
        Mockito.verify(callback, Mockito.never()).onMessage(any())
        Mockito.verify(callback, Mockito.never()).onEnded()
        Mockito.verify(callback, Mockito.never()).onStarted()
    }

    @Test
    fun testConnectionDropping_with_started_session() {
        controller.start()
        controller.join(500)

        controller.connectionAttemptFailed = false
        controller.messagesController.startSession(Random.nextBytes(1024))

        controller::class.java
                .getDeclaredMethod("socketClosed", Boolean::class.java)
                .apply { isAccessible = true }
                .invoke(controller, false)

        controller.interrupt()
        controller.join(500)

        assertFalse(controller.messagesController.isSessionStarted())
        Mockito.verify(callback, Mockito.never()).onMessage(any())
        Mockito.verify(callback).onEnded()
        Mockito.verify(callback, Mockito.never()).onStarted()
    }

    @Test
    fun testSendPacket() {
        val connectionSemaphore = Semaphore(0)
        val readableSemaphore = Semaphore(0)
        val receiveBuffer = ByteArray(11)

        startServer()
        thread {
            val socket = serverSocket.accept()
            connectionSemaphore.release()

            socket.getInputStream().read(receiveBuffer)
            readableSemaphore.release()
        }

        val bytes = byteArrayOf(1, 2, 3, 4, 5)
        val valid = byteArrayOf(0, 0, 11, 0, 0, 5) + bytes

        controller.start()
        connectionSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        controller.sendPacket(bytes)
        controller.join(1000)
        readableSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        assertArrayEquals(valid, receiveBuffer)
    }

    @Test
    fun testStartEncryptedSession() {
        val secretKey = Random.nextBytes(128)

        controller.startEncryptedSession(secretKey)
        assertTrue(controller.messagesController.isSessionStarted())
        Mockito.verify(callback, Mockito.never()).onMessage(any())
        Mockito.verify(callback, Mockito.never()).onEnded()
        Mockito.verify(callback).onStarted()
    }
}