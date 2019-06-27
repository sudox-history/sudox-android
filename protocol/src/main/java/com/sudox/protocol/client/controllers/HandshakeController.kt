package com.sudox.protocol.client.controllers

import com.sudox.encryption.ECDHSession
import com.sudox.encryption.Encryption
import com.sudox.protocol.client.ProtocolController

internal val OK = "ok".toByteArray()
internal const val KEY_SIZE = 24
internal const val HANDSHAKE_PACKET_NAME = "hsk"
internal const val HANDSHAKE_PACKET_PARTS_COUNT = 3

class HandshakeController(val protocolController: ProtocolController) {

    private var session: ECDHSession? = null

    fun start() {
        session = Encryption.startECDH()
        protocolController.sendPacket(arrayOf(HANDSHAKE_PACKET_NAME, session!!.publicKey))
    }

    fun handlePacket(parts: Array<*>) {
        val serverPublicKey = parts[1] as? ByteArray ?: return
        val serverPublicKeySign = parts[2] as? ByteArray ?: return
        val serverHmac = parts[3] as? ByteArray ?: return

        if (!Encryption.verifySignature(serverPublicKey, serverPublicKeySign)) {
            protocolController.restartConnection()
            return
        }

        val keyPairPointer = session!!.keyPairPointer
        var secretKey = Encryption.finishECDH(keyPairPointer, serverPublicKey)
        session = null

        if (secretKey == null) {
            protocolController.restartConnection()
            return
        }

        secretKey = secretKey.copyOf(KEY_SIZE)

        if (!Encryption.verifyHMAC(secretKey, OK, serverHmac)) {
            protocolController.restartConnection()
            return
        }

        protocolController.startSession(secretKey)
    }

    fun isPacket(name: String, parts: Array<*>): Boolean {
        // Size including name element
        return parts.size == HANDSHAKE_PACKET_PARTS_COUNT + 1 && name == HANDSHAKE_PACKET_NAME
    }

    fun reset() {
        if (session != null) {
            Encryption.closeECDH(session!!.keyPairPointer)
            session = null
        }
    }
}