package com.sudox.protocol

import android.support.test.runner.AndroidJUnit4
import com.sudox.protocol.ProtocolController.Companion.RECONNECT_ATTEMPTS_INTERVAL_IN_MILLIS
import com.sudox.protocol.controllers.HandshakeStatus
import com.sudox.protocol.models.enums.ConnectionState
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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

    @Before
    fun setUp() {
        port = Random.nextInt(1000, 32768)
        client = ProtocolClient("127.0.0.1", port.toShort())
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
    fun testReconnectionBeforeError() {
        var isConnectedToServer = false

        controller.start()
        controller.join(500)

        assertTrue(controller.connectionAttemptFailed)
        assertNull(client.connectionStateChannel.valueOrNull)

        startServer()
        thread {
            serverSocket.accept()
            isConnectedToServer = true
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        controller.join(RECONNECT_ATTEMPTS_INTERVAL_IN_MILLIS)
        assertTrue(isConnectedToServer)
        assertFalse(controller.connectionAttemptFailed)
    }

    @Test
    fun testConnectionDropping_without_handshake() {
        val disconnectionSemaphore = Semaphore(0)

        startServer()
        thread {
            val socket = serverSocket.accept()
            socket.close()
            disconnectionSemaphore.release()
        }.setUncaughtExceptionHandler { _, _ -> /** Ignore */ }

        controller.start()
        controller.join(500)

        disconnectionSemaphore.acquire()
        controller.interrupt()
        controller.join(500)

        // TODO: Testing reader resetting

        assertNull(client.connectionStateChannel.valueOrNull)
    }

    @Test
    fun testConnectionDropping_with_handshake() {
        controller.start()
        controller.join(500)

        controller.connectionAttemptFailed = false
        controller.handshakeController.handshakeStatus = HandshakeStatus.SUCCESS

        controller::class.java
                .getDeclaredMethod("socketClosed", Boolean::class.java)
                .apply { isAccessible = true }
                .invoke(controller, false)

        controller.interrupt()
        controller.join(500)

        // TODO: Testing reader, handshake resetting

        assertEquals(ConnectionState.CONNECTION_CLOSED, client.connectionStateChannel.valueOrNull)
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
        controller.join(500)
        readableSemaphore.tryAcquire(5, TimeUnit.SECONDS)

        assertArrayEquals(valid, receiveBuffer)
    }
}