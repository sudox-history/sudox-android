package com.sudox.protocol

import com.sudox.common.structures.QueueList
import com.sudox.common.threading.SequenceThread
import com.sudox.protocol.controllers.HandshakeController
import com.sudox.protocol.controllers.MessagesController
import com.sudox.protocol.controllers.PingController
import com.sudox.protocol.helpers.deserializePacket
import com.sudox.protocol.helpers.serializePacket
import com.sudox.sockets.SocketClient

internal const val RECONNECT_ATTEMPTS_INTERVAL_IN_MILLIS = 1000L

class ProtocolController(
    var protocolClient: ProtocolClient
) : SequenceThread("Sudox Protocol Controller"), SocketClient.ClientCallback {

    private val socketClient = SocketClient(protocolClient.host, protocolClient.port).apply {
        callback(this@ProtocolController)
    }

    private val protocolReader = ProtocolReader(socketClient)
    private val handshakeController = HandshakeController(this)
    private val pingController = PingController(this)
    private var connectionAttemptFailed: Boolean = true
    private val messagesController = MessagesController(this)

    override fun threadStart() = submitTask {
        socketClient.connect()
    }

    override fun threadStop() = submitTask {
        closeConnection()
    }

    override fun socketConnected() = submitTask {
        connectionAttemptFailed = false
        pingController.startPing()
        handshakeController.startHandshake()
    }

    override fun socketReceive() {
        val buffer = protocolReader.readPacketBytes() ?: return
        val slices = deserializePacket(buffer) ?: return
        pingController.schedulePingSendTask()

        if (slices.size() > 0) {
            handlePacket(slices)
        }
    }

    private fun handlePacket(slices: QueueList<ByteArray>) {
        val name = slices.pop()!!

        if (pingController.isPingPacket(name)) {
            pingController.handlePing()
        } else {
            addMessageToQueue(name, slices)
        }
    }

    private fun addMessageToQueue(name: ByteArray, slices: QueueList<ByteArray>) = submitTask {
        if (messagesController.isSessionStarted() && messagesController.isEncryptedMessagePacket(name, slices)) {
            messagesController.handleIncomingMessage(slices)
        } else if (handshakeController.isHandshakePacket(name, slices)) {
            handshakeController.handleIncomingPacket(slices)
        }
    }

    override fun socketClosed(needRestart: Boolean) = submitTask {
        val sessionStarted = messagesController.isSessionStarted()

        removeAllScheduledTasks()
        protocolReader.resetPacket()
        handshakeController.resetHandshake()
        messagesController.resetSession()

        if (sessionStarted && !connectionAttemptFailed) {
            protocolClient.callback.onEnded()
        }

        if (needRestart) {
            if (connectionAttemptFailed) {
                submitDelayedTask(RECONNECT_ATTEMPTS_INTERVAL_IN_MILLIS) { socketClient.connect() }
            } else {
                submitTask { socketClient.connect() }
            }

            connectionAttemptFailed = true
        }
    }

    internal fun sendMessage(message: ByteArray): Boolean {
        return messagesController.sendMessage(message)
    }

    internal fun sendPacket(vararg slices: ByteArray) {
        socketClient.sendBuffer(serializePacket(slices))
    }

    internal fun startEncryptedSession(secretKey: ByteArray) {
        messagesController.startSession(secretKey)
        protocolClient.callback.onStarted()
    }

    internal fun submitSessionMessageEvent(message: ByteArray) {
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