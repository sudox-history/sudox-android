package com.sudox.protocol.client.controllers

import com.sudox.common.structures.QueueList
import com.sudox.encryption.ECDHSession
import com.sudox.encryption.Encryption
import com.sudox.protocol.client.ProtocolController

internal val OK = "ok".toByteArray()
internal val HANDSHAKE_PACKET_NAME = "hsk".toByteArray()
internal const val KEY_SIZE = 24
internal const val HANDSHAKE_PACKET_PARTS_COUNT = 3

class HandshakeController(val protocolController: ProtocolController) {

    private var ecdhSession: ECDHSession? = null

    fun start() {
        ecdhSession = Encryption.startECDH()
        protocolController.sendPacket(HANDSHAKE_PACKET_NAME, ecdhSession!!.publicKey)
    }

    fun handlePacket(parts: QueueList<ByteArray>) {
        val serverPublicKey = parts.shift()!!
        val serverPublicKeySign = parts.shift()!!
        val serverHmac = parts.shift()!!

        if (!Encryption.verifySignature(serverPublicKey, serverPublicKeySign)) {
            protocolController.restartConnection()
            return
        }

        var secretKey = Encryption.finishECDH(ecdhSession!!.keyPairPointer, serverPublicKey)
        ecdhSession = null

        if (secretKey != null) {
            secretKey = secretKey.copyOf(KEY_SIZE)

            if (Encryption.verifyHMAC(secretKey, OK, serverHmac)) {
                protocolController.startSession(secretKey)
                return
            }
        }

        protocolController.restartConnection()
    }

    fun isPacket(name: ByteArray, parts: QueueList<ByteArray>): Boolean {
        return parts.size() == HANDSHAKE_PACKET_PARTS_COUNT && name.contentEquals(HANDSHAKE_PACKET_NAME)
    }

    fun reset() {
        if (ecdhSession != null) {
            Encryption.closeECDH(ecdhSession!!.keyPairPointer)
            ecdhSession = null
        }
    }
}