package com.sudox.protocol

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.Semaphore
import kotlin.random.Random

@RunWith(PowerMockRunner::class)
@PrepareForTest(ProtocolClient::class, ProtocolController::class, Socket::class)
class ProtocolWriterTest : Assert() {

    private lateinit var protocolClient: ProtocolClient
    private lateinit var protocolWriter: ProtocolWriter

    @Before
    fun setUp() {
        protocolClient = PowerMockito.mock(ProtocolClient::class.java)
        protocolClient.controller = PowerMockito.mock(ProtocolController::class.java)
        protocolClient.socket = Socket()
        protocolWriter = ProtocolWriter(protocolClient)
    }

    @After
    fun tearDown() {
        Mockito.reset(protocolClient)
        Mockito.reset(protocolClient.controller)
    }

    @Test
    fun testWrite_socket_ended_by_server() {
        Mockito.`when`(protocolClient.isValid()).thenReturn(true)

        // Preparing ...
        val semaphore = Semaphore(0)
        val randomPort = Random.nextInt(1, 65535)
        val serverThread = Thread {
            val serverSocket = ServerSocket(randomPort)
            val socket = serverSocket.accept()

            // Close connection ...
            semaphore.acquire()
            serverSocket.close()
            socket.close()
        }

        // Testing ...
        serverThread.start()
        protocolClient.socket!!.connect(InetSocketAddress(randomPort))
        protocolWriter.start()

        // Add data ...
        protocolWriter.addToQueue(ByteArray(1))
        semaphore.release()
        protocolWriter.addToQueue(ByteArray(1))
        protocolWriter.join(3000)

        // Verifying ...
        assertTrue((ProtocolWriter::class.java
                .getDeclaredField("messagesQueue")
                .apply { isAccessible = true }
                .get(protocolWriter) as Queue<ByteArray>)
                .isEmpty())

        protocolWriter.interrupt()
        serverThread.interrupt()
    }

    @Test
    fun testWrite_socket_ended_by_client() {
        Mockito.`when`(protocolClient.isValid()).thenReturn(true)

        // Preparing ...
        val semaphore = Semaphore(0)
        val randomPort = Random.nextInt(1, 65535)
        val serverThread = Thread {
            val serverSocket = ServerSocket(randomPort)
            serverSocket.accept()

            // Wait unblocking ...
            semaphore.acquire()

            // Close client socket ...
            protocolClient.socket!!.close()
        }

        // Testing ...
        serverThread.start()
        protocolClient.socket!!.connect(InetSocketAddress(randomPort))
        protocolWriter.start()
        protocolWriter.join(3000)
        protocolWriter.addToQueue(ByteArray(1))
        protocolWriter.addToQueue(ByteArray(1))
        semaphore.release()
        protocolWriter.addToQueue(ByteArray(1))
        protocolWriter.join(3000)

        // Verifying ...
        assertTrue((ProtocolWriter::class.java
                .getDeclaredField("messagesQueue")
                .apply { isAccessible = true }
                .get(protocolWriter) as Queue<ByteArray>)
                .isEmpty())

        protocolWriter.interrupt()
        serverThread.interrupt()
    }

    @Test
    fun testWrite_socket_write() {
        Mockito.`when`(protocolClient.isValid()).thenReturn(true)

        // Preparing ...
        val testData = ByteArray(1024) { '1'.toByte() }
        val buffer = ByteArray(testData.size)
        val randomPort = Random.nextInt(1, 65535)
        val serverThread = Thread {
            val serverSocket = ServerSocket(randomPort)
            val socket = serverSocket.accept()

            socket.getInputStream().read(buffer)
            serverSocket.close()
            socket.close()
        }

        // Testing ...
        serverThread.start()
        protocolClient.socket!!.connect(InetSocketAddress(randomPort))
        protocolWriter.start()
        protocolWriter.addToQueue(testData)
        protocolWriter.join(3000)

        // Verifying ...
        assertArrayEquals(testData, buffer)
        assertTrue((ProtocolWriter::class.java
                .getDeclaredField("messagesQueue")
                .apply { isAccessible = true }
                .get(protocolWriter) as Queue<ByteArray>)
                .isEmpty())

        protocolWriter.interrupt()
        serverThread.interrupt()
    }
}