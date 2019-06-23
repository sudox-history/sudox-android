package com.sudox.protocol.controllers

import com.sudox.common.structures.QueueList
import com.sudox.encryption.Encryption
import com.sudox.protocol.ProtocolController

internal val ENCRYPTED_MESSAGE_PACKET_NAME = "msg".toByteArray()
internal const val ENCRYPTED_MESSAGE_SLICE_COUNT = 3
internal const val ENCRYPTED_MESSAGE_IV_SIZE = 16
internal const val SALT_LENGTH_RANGE_START = 16
internal const val SALT_LENGTH_RANGE_END = 32

class MessagesController(val protocolController: ProtocolController) {

    private var secretKey: ByteArray? = null

    fun startSession(secretKey: ByteArray) {
        this.secretKey = secretKey
    }

    fun handleIncomingMessage(slices: QueueList<ByteArray>) {
        if (!handleEncryptedMessage(slices)) {
            protocolController.restartConnection()
        }
    }

    private fun handleEncryptedMessage(slices: QueueList<ByteArray>): Boolean {
        val cipher = slices.pop()!!
        val hmac = slices.pop()!!
        val iv = slices.pop()!!

        if (!Encryption.verifyHMAC(secretKey!!, cipher, hmac)) {
            return false
        }

        val messageWithSalt = Encryption.decryptWithAES(secretKey!!, iv, cipher) ?: return false
        val message = messageWithSalt.copyOfRange(0, messageWithSalt.size - messageWithSalt.last())
        protocolController.submitSessionMessageEvent(message)

        return true
    }

    fun sendMessage(message: ByteArray): Boolean {
        if (!isSessionStarted()) {
            return false
        }

        val saltLength = Encryption.generateInt(SALT_LENGTH_RANGE_START, SALT_LENGTH_RANGE_END)
        val salt = Encryption.generateBytes(saltLength)
        val messageWithSalt = message + salt + (saltLength + 1).toByte()
        val iv = Encryption.generateBytes(ENCRYPTED_MESSAGE_IV_SIZE)
        val cipher = Encryption.encryptWithAES(secretKey!!, iv, messageWithSalt)!!
        val hmac = Encryption.computeHMAC(secretKey!!, cipher)
        protocolController.sendPacket(ENCRYPTED_MESSAGE_PACKET_NAME, cipher, hmac, iv)

        return true
    }

    fun isEncryptedMessagePacket(name: ByteArray, slices: QueueList<ByteArray>): Boolean {
        return slices.size() == ENCRYPTED_MESSAGE_SLICE_COUNT && name.contentEquals(ENCRYPTED_MESSAGE_PACKET_NAME)
    }

    fun isSessionStarted(): Boolean {
        return secretKey != null
    }

    fun resetSession() {
        secretKey = null
    }
}