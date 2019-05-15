package com.sudox.protocol

import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

class ProtocolClient(val host: String, val port: Short) {

    var protocolController: ProtocolController? = null
    val connectionStateChannel = ConflatedBroadcastChannel<ConnectionState>()

    fun connect() {
        if (!isWorkerAlive()) {
            protocolController = getWorker()
            protocolController!!.start()
        }
    }

    fun close() {
        if (isWorkerAlive()) {
            protocolController!!.interrupt()
            protocolController = null
        }
    }

    private fun getWorker(): ProtocolController {
        return if (!isWorkerAlive()) {
            ProtocolController(this)
        } else {
            protocolController!!
        }
    }

    private fun isWorkerAlive(): Boolean {
        return protocolController != null &&
                !protocolController!!.isInterrupted &&
                protocolController!!.isAlive
    }

    internal fun submitStateChangeEvent(connectionState: ConnectionState) {
        connectionStateChannel.offer(connectionState)
    }
}