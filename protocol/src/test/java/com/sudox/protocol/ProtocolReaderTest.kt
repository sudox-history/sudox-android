package com.sudox.protocol

import org.junit.After
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
import java.util.concurrent.Semaphore
import kotlin.random.Random

@RunWith(PowerMockRunner::class)
@PrepareForTest(ProtocolClient::class, ProtocolController::class, Socket::class)
class ProtocolReaderTest {

    private lateinit var protocolClient: ProtocolClient
    private lateinit var protocolReader: ProtocolReader

    @Before
    fun setUp() {
        protocolClient = PowerMockito.mock(ProtocolClient::class.java)
        protocolClient.controller = PowerMockito.mock(ProtocolController::class.java)
        protocolClient.socket = Socket()
        protocolReader = ProtocolReader(protocolClient)
    }

    @After
    fun tearDown() {
        Mockito.reset(protocolClient)
        Mockito.reset(protocolClient.controller)
    }

    @Test
    fun testRead_socket_ended_by_server() {
        Mockito.`when`(protocolClient.isValid()).thenReturn(true)

        // Preparing ...
        val randomPort = Random.nextInt(1, 65535)
        val serverThread = Thread {
            val serverSocket = ServerSocket(randomPort)
            val socket = serverSocket.accept()

            // Close connection ...
            serverSocket.close()
            socket.close()
        }

        // Testing ...
        serverThread.start()
        protocolClient.socket!!.connect(InetSocketAddress(randomPort))
        protocolReader.start()
        protocolReader.join(3000)

        // Verifying ...
        Mockito.verify(protocolClient.controller)!!.onEnd()
        protocolReader.interrupt()
        serverThread.interrupt()
    }

    @Test
    fun testRead_socket_ended_by_client() {
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
        protocolReader.start()
        protocolReader.join(3000)
        semaphore.release()
        protocolReader.join(3000)

        // Verifying ...
        Mockito.verify(protocolClient.controller)!!.onEnd()
        protocolReader.interrupt()
        serverThread.interrupt()
    }

    @Test
    fun testRead_packets_in_one_buffer() {
        Mockito.`when`(protocolClient.isValid()).thenReturn(true)

        // Preparing ...
        val randomPort = Random.nextInt(1, 65535)
        val serverThread = Thread {
            val serverSocket = ServerSocket(randomPort)
            val socket = serverSocket.accept()

            // Wait unblocking ...
            socket.getOutputStream().write("[a][b][c][d][e][f]".toByteArray())
            socket.getOutputStream().flush()
        }

        // Testing ...
        serverThread.start()
        protocolClient.socket!!.connect(InetSocketAddress(randomPort))
        protocolReader.start()
        protocolReader.join(3000)

        // Verifying ...
        Mockito.verify(protocolClient.controller)!!.onPacket("[a]")
        Mockito.verify(protocolClient.controller)!!.onPacket("[b]")
        Mockito.verify(protocolClient.controller)!!.onPacket("[c]")
        Mockito.verify(protocolClient.controller)!!.onPacket("[d]")
        Mockito.verify(protocolClient.controller)!!.onPacket("[e]")
        Mockito.verify(protocolClient.controller)!!.onPacket("[f]")
        protocolReader.interrupt()
        serverThread.interrupt()
    }

    @Test
    fun testRead_single_packet_in_many_buffers() {
        Mockito.`when`(protocolClient.isValid()).thenReturn(true)

        // Preparing ...
        val firstBuffer = ByteArray(BUFFER_SIZE) {
            if (it == 0) {
                return@ByteArray '['.toByte()
            } else {
                return@ByteArray 'A'.toByte()
            }
        }

        val secondBuffer = ByteArray(BUFFER_SIZE) {
            return@ByteArray 'B'.toByte()
        }

        val thirdBuffer = ByteArray(BUFFER_SIZE / 2) {
            if (it != BUFFER_SIZE / 2 - 1) {
                return@ByteArray 'C'.toByte()
            } else {
                return@ByteArray ']'.toByte()
            }
        }

        val result = String(firstBuffer + secondBuffer + thirdBuffer)
        val randomPort = Random.nextInt(1, 65535)
        val serverThread = Thread {
            val serverSocket = ServerSocket(randomPort)
            val socket = serverSocket.accept()

            // Wait unblocking ...
            socket.getOutputStream().write(result.toByteArray())
            socket.getOutputStream().flush()
        }

        // Testing ...
        serverThread.start()
        protocolClient.socket!!.connect(InetSocketAddress(randomPort))
        protocolReader.start()
        protocolReader.join(3000)

        // Verifying ...
        Mockito.verify(protocolClient.controller)!!.onPacket(result)
        protocolReader.interrupt()
        serverThread.interrupt()
    }

    @Test
    fun testRead_many_packets_in_many_buffers() {
        Mockito.`when`(protocolClient.isValid()).thenReturn(true)

        // Preparing ...
        val firstBuffer = ByteArray(BUFFER_SIZE) {
            if (it == 0) {
                return@ByteArray '['.toByte()
            } else {
                return@ByteArray 'A'.toByte()
            }
        }

        val secondBuffer = ByteArray(BUFFER_SIZE / 2) {
            if (it != BUFFER_SIZE / 2 - 1) {
                return@ByteArray 'B'.toByte()
            } else {
                return@ByteArray ']'.toByte()
            }
        }

        val firstPacket = "[D]"
        val secondPacket = String(firstBuffer + secondBuffer)
        val randomPort = Random.nextInt(1, 65535)
        val serverThread = Thread {
            val serverSocket = ServerSocket(randomPort)
            val socket = serverSocket.accept()

            // Wait unblocking ...
            socket.getOutputStream().write((firstPacket + secondPacket).toByteArray())
            socket.getOutputStream().flush()
        }

        // Testing ...
        serverThread.start()
        protocolClient.socket!!.connect(InetSocketAddress(randomPort))
        protocolReader.start()
        protocolReader.join(3000)

        // Verifying ...
        Mockito.verify(protocolClient.controller)!!.onPacket(firstPacket)
        Mockito.verify(protocolClient.controller)!!.onPacket(secondPacket)
        protocolReader.interrupt()
        serverThread.interrupt()
    }
}