package com.sudox.protocol.client

class ProtocolClient(
    internal val host: String,
    internal val port: Short,
    internal val callback: ProtocolCallback
) {

    internal var protocolController: ProtocolController? = null

    fun connect() {
        if (!isControllerAlive()) {
            protocolController = getController()
            protocolController!!.start()
        }
    }

    fun close() {
        if (isControllerAlive()) {
            protocolController!!.interrupt()
            protocolController = null
        }
    }
    
    fun sendMessage(message: ByteArray): Boolean {
        if (!isControllerAlive()) {
            return false
        }

        return protocolController!!.sendMessage(message)
    }

    private fun getController(): ProtocolController {
        return if (!isControllerAlive()) {
            ProtocolController(this)
        } else {
            protocolController!!
        }
    }

    private fun isControllerAlive(): Boolean {
        return protocolController != null &&
                !protocolController!!.isInterrupted &&
                protocolController!!.isAlive
    }
}