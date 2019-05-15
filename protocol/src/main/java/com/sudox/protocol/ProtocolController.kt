package com.sudox.protocol

import com.sudox.common.async.ControllerThread
import com.sudox.protocol.controllers.HandshakeController
import com.sudox.protocol.helpers.deserializePacketSlices
import com.sudox.protocol.helpers.serializePacket
import com.sudox.protocol.models.enums.ConnectionState
import com.sudox.sockets.SocketClient

class ProtocolController(var protocolClient: ProtocolClient) :
        ControllerThread("STIPS Worker"),
        SocketClient.ClientCallback {

    internal val socketClient = SocketClient(protocolClient.host, protocolClient.port).apply {
        callback(this@ProtocolController)
    }

    internal var connectionAttemptFailed: Boolean = true
    internal val reader = ProtocolReader(socketClient)
    internal val handshakeController = HandshakeController(this)

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
        handshakeController.startHandshake()
    }

    override fun socketReceive() {
        val buffer = reader.readPacketBytes() ?: return
        val slices = deserializePacketSlices(buffer) ?: return

        if (slices.isEmpty()) {
            return
        }

        submitTask {
            if (handshakeController.isHandshakeSucceed()) {
                // TODO: Messages controller
            } else {
                handshakeController.handleIncomingMessage(slices)
            }
        }
    }

    override fun socketClosed(needRestart: Boolean) = submitTask {
        val handshakeSucceed = handshakeController.isHandshakeSucceed()

        removeAllPlannedTasks()
        reader.resetPacket()
        handshakeController.resetHandshake()

        if (handshakeSucceed) {
            submitConnectionClosedEvent()
        }

        if (needRestart) {
            submitReconnectTask()
        }
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

    private fun submitConnectionClosedEvent() {
        if (!connectionAttemptFailed) {
            protocolClient.submitStateChangeEvent(ConnectionState.CONNECTION_CLOSED)
        }
    }

    internal fun submitConnectSucceedEvent() {
        protocolClient.submitStateChangeEvent(ConnectionState.CONNECT_SUCCEED)
    }

    internal fun closeConnection() {
        socketClient.close(false)
    }

    internal fun restartConnection() {
        // Connection will be recreated if it will be closed by error reason
        socketClient.close(true)
    }
}