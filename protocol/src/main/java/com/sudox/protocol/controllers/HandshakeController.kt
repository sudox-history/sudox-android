package com.sudox.protocol.controllers

import com.sudox.encryption.Encryption
import com.sudox.protocol.ProtocolController
import java.util.LinkedList

internal const val PUBLIC_KEY_MESSAGE_SLICES_COUNT = 3
internal val HMAC_VALIDATION_WORD = "ok".toByteArray()

class HandshakeController(val protocolController: ProtocolController) {

    @HandshakeStatus
    internal var handshakeStatus = HandshakeStatus.NOT_STARTED
    private var ownPublicKey: ByteArray? = null
    private var ownPrivateKey: ByteArray? = null

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
        if (!Encryption.verifyMessageWithECDSA(serverPublicKey, serverPublicKeySign)) {
            return false
        }

        // Second & third steps - calculating secret key and hmac
        val secretKey = Encryption.calculateSecretKey(ownPrivateKey!!, serverPublicKey) ?: return false
        val hmac = Encryption.calculateHMAC(secretKey, HMAC_VALIDATION_WORD)

        // Fourth step - comparing server and own hmacs
        if (!Encryption.checkEqualsAllBytes(hmac, serverHmac)) {
            return false
        }

        handshakeStatus = HandshakeStatus.SUCCESS
        protocolController.startEncryptedSession(secretKey)
        resetKeys()

        return true
    }

    internal fun generateKeysPair() {
        val keysPairId = Encryption.generateKeysPair()

        ownPublicKey = Encryption.getPublicKey(keysPairId)
        ownPrivateKey = Encryption.getPrivateKey(keysPairId)

        Encryption.removeKeysPair(keysPairId)
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