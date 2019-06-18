package com.sudox.protocol

import androidx.annotation.VisibleForTesting
import com.sudox.common.threading.ControllerThread
import com.sudox.protocol.controllers.HandshakeController
import com.sudox.protocol.controllers.MessagesController
import com.sudox.protocol.controllers.PingController
import com.sudox.protocol.helpers.deserializePacketSlices
import com.sudox.protocol.helpers.serializePacket
import com.sudox.sockets.SocketClient
import java.util.LinkedList

@VisibleForTesting
const val RECONNECT_ATTEMPTS_INTERVAL_IN_MILLIS = 1000L

class ProtocolController(
    var protocolClient: ProtocolClient
) : ControllerThread("STIPS Worker"), SocketClient.ClientCallback {

    @VisibleForTesting
    val socketClient = SocketClient(protocolClient.host, protocolClient.port).apply {
        callback(this@ProtocolController)
    }

    @VisibleForTesting
    var connectionAttemptFailed: Boolean = true
    @VisibleForTesting
    internal val protocolReader = ProtocolReader(socketClient)
    @VisibleForTesting
    val handshakeController = HandshakeController(this)
    @VisibleForTesting
    val messagesController = MessagesController(this)
    @VisibleForTesting
    val pingController = PingController(this)

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
        handlePacket(slices)
    }

    private fun handlePacket(slices: LinkedList<ByteArray>) {
        val name = slices.remove()

        if (pingController.isPingPacket(name)) {
            pingController.handlePing()
        } else {
            addMessageToQueue(name, slices)
        }
    }

    private fun addMessageToQueue(name: ByteArray, slices: LinkedList<ByteArray>) = submitTask {
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

    private fun submitSessionStartedEvent() {
        protocolClient.callback.onStarted()
    }

    private fun submitSessionEndedEvent() {
        if (!connectionAttemptFailed) {
            protocolClient.callback.onEnded()
        }
    }

    fun submitSessionMessageEvent(message: ByteArray) {
        protocolClient.callback.onMessage(message)
    }

    fun closeConnection() {
        socketClient.close(false)
    }

    internal fun restartConnection() {
        // Connection will be recreated if it will be closed by error reason
        socketClient.close(true)
    }
}