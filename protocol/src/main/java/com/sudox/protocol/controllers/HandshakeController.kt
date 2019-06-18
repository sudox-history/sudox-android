package com.sudox.protocol.controllers

import androidx.annotation.VisibleForTesting
import com.sudox.encryption.Encryption
import com.sudox.protocol.ProtocolController
import java.util.LinkedList

internal val HMAC_VALIDATION_WORD = "ok".toByteArray()
internal val HANDSHAKE_MESSAGE_NAME = byteArrayOf(0, 0, 10)
internal const val HANDSHAKE_MESSAGE_SLICES_COUNT = 3

class HandshakeController(val protocolController: ProtocolController) {

    private var ownPublicKey: ByteArray? = null
    private var ownPrivateKey: ByteArray? = null

    fun startHandshake() {
        generateKeysPair()
        protocolController.sendPacket(HANDSHAKE_MESSAGE_NAME, ownPublicKey!!)
    }

    fun handleIncomingPacket(slices: LinkedList<ByteArray>) {
        if (!handleHandshakePacket(slices)) {
            protocolController.restartConnection()
        }
    }

    /**
     * Returns false if errors occurred.
     * Returns true & starting session if handshake successfully completed
     */
    @VisibleForTesting
    fun handleHandshakePacket(slices: LinkedList<ByteArray>): Boolean {
        val serverPublicKey = slices.remove()
        val serverPublicKeySign = slices.remove()
        val serverHmac = slices.remove()

        if (!Encryption.verifyMessageWithECDSA(serverPublicKey, serverPublicKeySign)) {
            return false
        }

        val secretKey = Encryption.calculateSecretKey(ownPrivateKey!!, serverPublicKey)
                ?: return false
        val hmac = Encryption.calculateHMAC(secretKey, HMAC_VALIDATION_WORD)
        if (!Encryption.checkEqualsAllBytes(hmac, serverHmac)) {
            return false
        }

        protocolController.startEncryptedSession(secretKey)
        resetHandshake()
        return true
    }

    @VisibleForTesting
    fun generateKeysPair() {
        val keysPairId = Encryption.generateKeysPair()
        ownPublicKey = Encryption.getPublicKey(keysPairId)
        ownPrivateKey = Encryption.getPrivateKey(keysPairId)
        Encryption.removeKeysPair(keysPairId)
    }

    fun isHandshakePacket(name: ByteArray, slices: LinkedList<ByteArray>): Boolean {
        return slices.size == HANDSHAKE_MESSAGE_SLICES_COUNT && name.contentEquals(HANDSHAKE_MESSAGE_NAME)
    }

    fun resetHandshake() {
        ownPublicKey = null
        ownPrivateKey = null
    }
}