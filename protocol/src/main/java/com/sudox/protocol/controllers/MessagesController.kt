package com.sudox.protocol.controllers

import com.sudox.common.structures.QueueList
import com.sudox.encryption.Encryption
import com.sudox.protocol.ProtocolController

internal val MESSAGES_PACKET_NAME = "msg".toByteArray()
internal const val MESSAGES_PARTS_COUNT = 3
internal const val IV_SIZE = 16
internal const val SALT_LENGTH_RANGE_START = 16
internal const val SALT_LENGTH_RANGE_END = 32

class MessagesController(val protocolController: ProtocolController) {

    private var secretKey: ByteArray? = null

    fun startSession(secretKey: ByteArray) {
        this.secretKey = secretKey
    }

    fun handle(parts: QueueList<ByteArray>) {
        val cipher = parts.pop()!!
        val hmac = parts.pop()!!
        val iv = parts.pop()!!

        if (!Encryption.verifyHMAC(secretKey!!, cipher, hmac)) {
            protocolController.restartConnection()
            return
        }

        val messageWithSalt = Encryption.decryptWithAES(secretKey!!, iv, cipher)
        if (messageWithSalt == null) {
            protocolController.restartConnection()
            return
        }

        val message = messageWithSalt.copyOfRange(0, messageWithSalt.size - messageWithSalt.last() - 1)
        protocolController.submitSessionMessageEvent(message)
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
        protocolController.sendPacket(MESSAGES_PACKET_NAME, ciphertext, hmac, iv)

        return true
    }

    fun isPacket(name: ByteArray, parts: QueueList<ByteArray>): Boolean {
        return parts.size() == MESSAGES_PARTS_COUNT && name.contentEquals(MESSAGES_PACKET_NAME)
    }

    fun isSessionStarted(): Boolean {
        return secretKey != null
    }

    fun resetSession() {
        secretKey = null
    }
}