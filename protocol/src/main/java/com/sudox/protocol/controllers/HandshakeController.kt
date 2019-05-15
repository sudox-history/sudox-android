package com.sudox.protocol.controllers

import com.sudox.cipher.Cipher
import com.sudox.protocol.ProtocolController
import java.util.LinkedList

class HandshakeController(val protocolController: ProtocolController) {

    @HandshakeStatus
    internal var handshakeStatus = HandshakeStatus.NOT_STARTED
    private var ownSecretKey: ByteArray? = null
    private var ownPublicKey: ByteArray? = null
    private var ownPrivateKey: ByteArray? = null

    companion object {
        private const val PUBLIC_KEY_MESSAGE_SLICES_COUNT = 3
    }

    fun startHandshake() {
        generateKeysPair()

        handshakeStatus = HandshakeStatus.WAIT_SERVER_PUBLIC_KEY
        protocolController.sendPacket(ownPublicKey!!)
    }

    fun handleIncomingMessage(slices: LinkedList<ByteArray>) {
        if (handshakeStatus == HandshakeStatus.WAIT_SERVER_PUBLIC_KEY) {
            if (handlePublicKeyMessage(slices)) {
                return
            }
        }

        protocolController.restartConnection()
    }

    /**
     * Returns false if errors occurred.
     * Returns true if handshake successfully completed
     */
    internal fun handlePublicKeyMessage(slices: LinkedList<ByteArray>): Boolean {
        if (ownPrivateKey == null || slices.size < PUBLIC_KEY_MESSAGE_SLICES_COUNT) {
            return false
        }

        val serverPublicKey = slices.remove()
        val serverPublicKeySign = slices.remove()
        val serverOkHmac = slices.remove()

        // First step - verify public key
        if (!Cipher.verifyMessageWithECDSA(serverPublicKey, serverPublicKeySign)) {
            return false
        }

        // Second step - calculating secret key
        val secretKey = Cipher.calculateSecretKey(ownPrivateKey, ownPublicKey)

        if (secretKey.isEmpty()) {
            return false
        }

        // Third step - calculating hmac using generated secret key
        val okHmac = Cipher.calculateHMAC(secretKey, "ok".toByteArray())

        // Fourth step - comparing server and own hmacs
        if (!Cipher.checkEqualsAllBytes(okHmac, serverOkHmac)) {
            return false
        }

        ownSecretKey = secretKey
        handshakeStatus = HandshakeStatus.SUCCESS
        protocolController.submitConnectSucceedEvent()

        return true
    }

    internal fun generateKeysPair() {
        val keysPairId = Cipher.generateKeysPair()

        ownPublicKey = Cipher.getPublicKey(keysPairId)
        ownPrivateKey = Cipher.getPrivateKey(keysPairId)

        Cipher.removeKeysPair(keysPairId)
    }

    internal fun resetHandshake() {
        handshakeStatus = HandshakeStatus.NOT_STARTED
        ownPublicKey = null
        ownPrivateKey = null
    }

    internal fun isHandshakeSucceed(): Boolean {
        return handshakeStatus == HandshakeStatus.SUCCESS
    }
}