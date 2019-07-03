package com.sudox.protocol.client

import com.sudox.protocol.client.controllers.KEY_SIZE
import com.sudox.protocol.client.controllers.PING_SEND_TIMEOUT
import com.sudox.protocol.client.network.TestSocketServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private var SECRET_KEY = Random.nextBytes(KEY_SIZE)
private const val DISCONNECT_TIMEOUT = PING_SEND_TIMEOUT + PING_SEND_TIMEOUT + 5000

class ProtocolControllerInstrumentedTest : Assert() {

    private lateinit var server: TestSocketServer
    private lateinit var protocolController: ProtocolController
    private lateinit var protocolClient: ProtocolClient
    private lateinit var protocolCallback: ProtocolCallbackMock

    @Before
    fun setUp() {
        server = TestSocketServer(4899)
        protocolCallback = ProtocolCallbackMock()
        protocolClient = ProtocolClient("127.0.0.1", 4899, protocolCallback)
        protocolController = ProtocolController(protocolClient)
    }

    @After
    fun tearDown() {
        DISABLE_PING = false

        protocolController.closeConnection()
        protocolController.interrupt()
        server.stopServer()
    }

    @Test
    fun testCloseConnection() {
        DISABLE_PING = true

        server.startServer()

        protocolController.start()
        server.connectionSemaphore.tryAcquire(5L, TimeUnit.SECONDS)
        assertTrue(server.clientConnected)

        protocolController.closeConnection()
        server.disconnectionSemaphore.tryAcquire(5L, TimeUnit.SECONDS)
        assertFalse(server.clientConnected)

        server.connectionSemaphore.tryAcquire(5L, TimeUnit.SECONDS)
        assertFalse(server.clientConnected)
    }

    @Test
    fun testRestartConnection() {
        DISABLE_PING = true

        server.startServer()

        protocolController.start()
        server.connectionSemaphore.tryAcquire(5L, TimeUnit.SECONDS)
        assertTrue(server.clientConnected)

        protocolController.restartConnection()
        server.disconnectionSemaphore.tryAcquire(5L, TimeUnit.SECONDS)
        assertFalse(server.clientConnected)

        server.connectionSemaphore.tryAcquire(5L, TimeUnit.SECONDS)
        assertTrue(server.clientConnected)
    }

    @Test
    fun testSessionNotStartedButConnectionEnded() {
        server.startServer()

        protocolController.start()
        server.connectionSemaphore.tryAcquire(5L, TimeUnit.SECONDS)
        assertTrue(server.clientConnected)

        server.stopServer()
        protocolCallback.endedSemaphore.tryAcquire(DISCONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        assertEquals(0, protocolCallback.endedCount)
    }

    @Test
    fun testConnectionEnded() {
        server.startServer()

        protocolController.start()
        server.connectionSemaphore.tryAcquire(5L, TimeUnit.SECONDS)
        assertTrue(server.clientConnected)

        protocolController.startSession(SECRET_KEY)
        server.stopServer()

        protocolCallback.endedSemaphore.tryAcquire(DISCONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        assertEquals(1, protocolCallback.endedCount)
    }
}