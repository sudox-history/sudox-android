package com.sudox.protocol

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

    /**
     * Returns false if message not sent
     * Returns true if message sent
     */
    fun sendMessage(message: ByteArray): Boolean {
        if (!isControllerAlive()) {
            return false
        }

        return protocolController!!.sendEncryptedMessage(message)
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