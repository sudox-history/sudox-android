package com.sudox.protocol.client.controllers

import com.sudox.encryption.Encryption
import com.sudox.protocol.client.ProtocolController

internal const val MESSAGES_PACKET_NAME = "msg"
internal const val MESSAGES_PARTS_COUNT = 3
internal const val IV_SIZE = 16
internal const val SALT_LENGTH_RANGE_START = 16
internal const val SALT_LENGTH_RANGE_END = 32

class MessagesController(val protocolController: ProtocolController) {

    private var secretKey: ByteArray? = null

    fun start(secretKey: ByteArray) {
        this.secretKey = secretKey
    }

    fun handlePacket(parts: Array<*>) {
        val cipher = parts[1] as? ByteArray ?: return
        val hmac = parts[2] as? ByteArray ?: return
        val iv = parts[3] as? ByteArray ?: return

        if (!Encryption.verifyHMAC(secretKey!!, cipher, hmac)) {
            protocolController.restartConnection()
            return
        }

        val messageWithSalt = Encryption.decryptWithAES(secretKey!!, iv, cipher)

        if (messageWithSalt == null) {
            protocolController.restartConnection()
            return
        }

        // Including byte of salt length
        val messageEndIndex = messageWithSalt.size - messageWithSalt.last() - 1
        val message = messageWithSalt.copyOfRange(0, messageEndIndex)
        protocolController.submitMessageEvent(message)
    }

    fun send(message: ByteArray): Boolean {
        if (!isSessionStarted()) {
            return false
        }

        val saltLength = Encryption.generateInt(SALT_LENGTH_RANGE_START, SALT_LENGTH_RANGE_END)
        val salt = Encryption.generateBytes(saltLength)
        val messageWithSalt = message + salt + saltLength.toByte()

        val iv = Encryption.generateBytes(IV_SIZE)
        val ciphertext = Encryption.encryptWithAES(secretKey!!, iv, messageWithSalt)!!
        val hmac = Encryption.computeHMAC(secretKey!!, ciphertext)

        protocolController.sendPacket(arrayOf(MESSAGES_PACKET_NAME, ciphertext, hmac, iv))
        return true
    }

    fun isPacket(name: String, parts: Array<*>): Boolean {
        // Size including name element
        return parts.size == MESSAGES_PARTS_COUNT + 1 && name == MESSAGES_PACKET_NAME
    }

    fun isSessionStarted(): Boolean {
        return secretKey != null
    }

    fun reset() {
        secretKey = null
    }
}