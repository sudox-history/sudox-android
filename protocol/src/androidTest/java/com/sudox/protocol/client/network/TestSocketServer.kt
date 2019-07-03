package com.sudox.protocol.client.network

import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.util.concurrent.Semaphore

class TestSocketServer(val port: Int) : Thread() {

    val connectionSemaphore = Semaphore(0)
    val disconnectionSemaphore = Semaphore(0)
    var clientConnected: Boolean = false

    var buffer: ByteBuffer? = null
    val receivingSemaphore = Semaphore(0)

    var serverSocket: ServerSocket? = null
    var clientSocket: Socket? = null

    fun startServer() {
        serverSocket = ServerSocket()
        serverSocket!!.bind(InetSocketAddress(port))
        start()
    }

    override fun run() {
        while (!isInterrupted) {
            handleClient()
        }
    }

    private fun handleClient() {
        try {
            clientSocket = serverSocket!!.accept()
            clientSocket!!.tcpNoDelay = true
            clientConnected = true
            connectionSemaphore.release()

            handleReceiving()
        } catch (e: Exception) {
            handleDisconnect()
        }
    }

    private fun handleReceiving() {
        while (!isInterrupted) {
            val read = clientSocket!!.getInputStream().read()

            if (read == -1) {
                handleDisconnect()
                break
            }

            // Reading ...
            buffer?.put(read.toByte())

            if (buffer?.remaining() == 0) {
                receivingSemaphore.release()
            }
        }
    }

    private fun handleDisconnect() {
        clientConnected = false
        disconnectionSemaphore.release()
    }

    fun stopServer() {
        serverSocket?.close()
    }

    fun send(bytes: ByteArray) {
        clientSocket?.getOutputStream()!!.write(bytes)
        clientSocket?.getOutputStream()!!.flush()
    }
}