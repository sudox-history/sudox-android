package com.sudox.protocol

import com.sudox.common.async.ControllerThread
import com.sudox.protocol.controllers.HandshakeController
import com.sudox.protocol.controllers.MessagesController
import com.sudox.protocol.controllers.PingController
import com.sudox.protocol.helpers.deserializePacketSlices
import com.sudox.protocol.helpers.serializePacket
import com.sudox.sockets.SocketClient
import java.util.LinkedList

class ProtocolController(var protocolClient: ProtocolClient) :
        ControllerThread("STIPS Worker"),
        SocketClient.ClientCallback {

    internal val socketClient = SocketClient(protocolClient.host, protocolClient.port).apply {
        callback(this@ProtocolController)
    }

    internal var connectionAttemptFailed: Boolean = true
    internal val protocolReader = ProtocolReader(socketClient)
    internal val handshakeController = HandshakeController(this)
    internal val messagesController = MessagesController(this)
    internal val pingController = PingController(this)

    companion object {
        const val RECONNECT_ATTEMPTS_INTERVAL_IN_MILLIS = 1000L
    }

    override fun threadStart() = submitTask {
        socketClient.connect()
    }

    override fun threadStop() = submitTask {
        socketClient.close(false)
    }

    override fun socketConnected() = submitTask {
        connectionAttemptFailed = false
        pingController.startPingCycle()
        handshakeController.startHandshake()
    }

    override fun socketReceive() {
        val buffer = protocolReader.readPacketBytes() ?: return
        val slices = deserializePacketSlices(buffer) ?: return

        if (slices.isEmpty()) {
            return
        }

        pingController.schedulePingSendTask()

        if (pingController.isPingPacket(slices)) {
            pingController.handlePing()
        } else {
            addMessageToQueue(slices)
        }
    }

    fun addMessageToQueue(slices: LinkedList<ByteArray>) = submitTask {
        if (messagesController.isSessionStarted()) {
            messagesController.handleIncomingMessage(slices)
        } else {
            handshakeController.handleIncomingMessage(slices)
        }
    }

    override fun socketClosed(needRestart: Boolean) = submitTask {
        val sessionStarted = messagesController.isSessionStarted()

        removeAllScheduledTasks()
        protocolReader.resetPacket()
        handshakeController.resetHandshake()
        messagesController.secretKey = null

        if (sessionStarted) {
            submitSessionEndedEvent()
        }

        if (needRestart) {
            submitReconnectTask()
        }
    }

    /**
     * Returns false if message not sent
     * Returns true if message sent
     */
    fun sendEncryptedMessage(message: ByteArray): Boolean {
        return messagesController.sendEncryptedMessage(message)
    }

    fun sendPacket(vararg slices: ByteArray) {
        socketClient.sendBuffer(serializePacket(slices))
    }

    /**
     * Submits reconnect task following rules:
     *
     * 1) if connection dropped - install connection now
     * 2) if previous connection attempt failed - install connection thought interval
     */
    private fun submitReconnectTask() {
        if (connectionAttemptFailed) {
            submitDelayedTask(RECONNECT_ATTEMPTS_INTERVAL_IN_MILLIS) { socketClient.connect() }
        } else {
            submitTask { socketClient.connect() }
        }

        connectionAttemptFailed = true
    }

    internal fun startEncryptedSession(secretKey: ByteArray) {
        messagesController.secretKey = secretKey
        submitSessionStartedEvent()
    }

    internal fun submitSessionStartedEvent() {
        protocolClient.callback.onStarted()
    }

    internal fun submitSessionEndedEvent() {
        if (!connectionAttemptFailed) {
            protocolClient.callback.onEnded()
        }
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