package com.sudox.protocol.controllers

import com.sudox.cipher.Cipher
import com.sudox.protocol.ProtocolController
import java.util.LinkedList

class HandshakeController(val protocolController: ProtocolController) {

    @HandshakeStatus
    internal var handshakeStatus = HandshakeStatus.NOT_STARTED
    private var ownPublicKey: ByteArray? = null
    private var ownPrivateKey: ByteArray? = null

    companion object {
        internal const val PUBLIC_KEY_MESSAGE_SLICES_COUNT = 3
        internal val HMAC_VALIDATION_WORD = "ok".toByteArray()
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
     * Returns true & starting session if handshake successfully completed
     */
    internal fun handlePublicKeyMessage(slices: LinkedList<ByteArray>): Boolean {
        if (slices.size != PUBLIC_KEY_MESSAGE_SLICES_COUNT) {
            return false
        }

        val serverPublicKey = slices.remove()
        val serverPublicKeySign = slices.remove()
        val serverHmac = slices.remove()

        // First step - verify public key
        if (!Cipher.verifyMessageWithECDSA(serverPublicKey, serverPublicKeySign)) {
            return false
        }

        // Second step - calculating secret key
        val secretKey = Cipher.calculateSecretKey(ownPrivateKey, serverPublicKey)

        if (secretKey.isEmpty()) {
            return false
        }

        // Third step - calculating hmac using generated secret key
        val hmac = Cipher.calculateHMAC(secretKey, HMAC_VALIDATION_WORD)

        // Fourth step - comparing server and own hmacs
        if (!Cipher.checkEqualsAllBytes(hmac, serverHmac)) {
            return false
        }

        handshakeStatus = HandshakeStatus.SUCCESS
        protocolController.startEncryptedSession(secretKey)
        resetKeys()

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
        resetKeys()
    }

    internal fun resetKeys() {
        ownPublicKey = null
        ownPrivateKey = null
    }
}