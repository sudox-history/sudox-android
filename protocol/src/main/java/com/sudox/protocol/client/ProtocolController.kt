package com.sudox.protocol.client

import com.sudox.common.threading.SequenceThread
import com.sudox.protocol.client.controllers.HandshakeController
import com.sudox.protocol.client.controllers.MessagesController
import com.sudox.protocol.client.controllers.PingController
import com.sudox.protocol.client.controllers.PacketController
import com.sudox.protocol.client.network.SocketCallback
import com.sudox.protocol.client.network.SocketClient

internal const val RECONNECT_ATTEMPTS_INTERVAL_IN_MILLIS = 1000L
internal var DISABLE_PING = false // Don't touch this variable! It's using only for testing.

class ProtocolController(
    var protocolClient: ProtocolClient
) : SequenceThread("Sudox Protocol Controller"), SocketCallback {

    private val socketClient = SocketClient(protocolClient.host, protocolClient.port).apply {
        callback(this@ProtocolController)
    }

    private val packetController = PacketController(socketClient)
    private val handshakeController = HandshakeController(this)
    private val pingController = PingController(this)
    private val messagesController = MessagesController(this)
    private var connectionAttemptFailed: Boolean = true

    override fun threadStart() = submitTask {
        socketClient.connect()
    }

    override fun threadStop() = submitTask {
        closeConnection()
    }

    override fun socketConnected() = submitTask {
        connectionAttemptFailed = false

        if (!DISABLE_PING) {
            pingController.start()
        }

        handshakeController.start()
    }

    override fun socketReceive() {
        val packet = packetController.readPacket() ?: return

        pingController.scheduleSendTask()
        handlePacket(packet)
    }

    private fun handlePacket(parts: Any) {
        if (parts is Array<*> && parts.size >= 1) {
            val name = parts[0] as? String ?: return

            if (pingController.isPacket(name)) {
                pingController.handlePacket()
            } else {
                addMessageToQueue(name, parts)
            }
        }
    }

    private fun addMessageToQueue(name: String, parts: Array<*>) = submitTask {
        if (messagesController.isSessionStarted() && messagesController.isPacket(name, parts)) {
            messagesController.handlePacket(parts)
        } else if (handshakeController.isPacket(name, parts)) {
            handshakeController.handlePacket(parts)
        }
    }

    override fun socketClosed(error: Boolean) = submitTask {
        val sessionStarted = messagesController.isSessionStarted()

        removeAllScheduledTasks()
        packetController.resetReading()
        handshakeController.reset()
        messagesController.reset()

        if (sessionStarted && !connectionAttemptFailed) {
            protocolClient.callback.onEnded()
        }

        if (error) {
            if (connectionAttemptFailed) {
                submitDelayedTask(RECONNECT_ATTEMPTS_INTERVAL_IN_MILLIS) { socketClient.connect() }
            } else {
                submitTask { socketClient.connect() }
            }

            connectionAttemptFailed = true
        }
    }

    internal fun sendMessage(message: ByteArray): Boolean {
        return messagesController.send(message)
    }

    internal fun sendPacket(parts: Array<Any>, urgent: Boolean = false) {
        packetController.sendPacket(parts, urgent)
    }

    internal fun startSession(secretKey: ByteArray) {
        messagesController.start(secretKey)
        protocolClient.callback.onStarted()
    }

    internal fun submitMessageEvent(message: ByteArray) {
        protocolClient.callback.onMessage(message)
    }

    internal fun closeConnection() {
        socketClient.close(false)
    }

    internal fun restartConnection() {
        // Connection will be recreated if it will be closed by error reason
        socketClient.close(true)
    }
}