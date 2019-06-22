package com.sudox.protocol.controllers

import com.sudox.common.structures.QueueList
import com.sudox.encryption.ECDHSession
import com.sudox.encryption.Encryption
import com.sudox.protocol.ProtocolController

internal val HMAC_VALIDATION_WORD = "ok".toByteArray()
internal val HANDSHAKE_MESSAGE_NAME = byteArrayOf(0, 0, 10)
internal const val HANDSHAKE_MESSAGE_SLICES_COUNT = 3

class HandshakeController(val protocolController: ProtocolController) {

    private var ecdhSession: ECDHSession? = null

    fun startHandshake() {
        ecdhSession = Encryption.startECDH()
        protocolController.sendPacket(HANDSHAKE_MESSAGE_NAME, ecdhSession!!.publicKey)
    }

    fun handleIncomingPacket(slices: QueueList<ByteArray>) {
        if (!handleHandshakePacket(slices)) {
            protocolController.restartConnection()
        }
    }

    private fun handleHandshakePacket(slices: QueueList<ByteArray>): Boolean {
        val serverPublicKey = slices.pop()!!
        val serverPublicKeySign = slices.pop()!!
        val serverHmac = slices.pop()!!

        if (!Encryption.verifySignature(serverPublicKey, serverPublicKeySign)) {
            return false
        }

        val secretKey = Encryption.finishECDH(ecdhSession!!.keyPairPointer, serverPublicKey) ?: return false

        if (Encryption.verifyHMAC(secretKey, HMAC_VALIDATION_WORD, serverHmac)) {
            protocolController.startEncryptedSession(secretKey)
            return true
        }

        return false
    }

    fun isHandshakePacket(name: ByteArray, slices: QueueList<ByteArray>): Boolean {
        return slices.size() == HANDSHAKE_MESSAGE_SLICES_COUNT && name.contentEquals(HANDSHAKE_MESSAGE_NAME)
    }

    fun resetHandshake() {
        if (ecdhSession != null) {
            Encryption.closeECDH(ecdhSession!!.keyPairPointer)
        }
    }
}