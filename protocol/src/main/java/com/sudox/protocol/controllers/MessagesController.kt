package com.sudox.protocol.controllers

import com.sudox.common.structures.QueueList
import com.sudox.encryption.Encryption
import com.sudox.protocol.ProtocolController

internal val ENCRYPTED_MESSAGE_NAME = byteArrayOf(0, 10, 0)
internal const val ENCRYPTED_MESSAGE_SLICE_COUNT = 3
internal const val ENCRYPTED_MESSAGE_IV_SIZE = 16

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

    /**
     * Returns true if message successfully decrypted
     * Returns false if error thrown
     */
    private fun handleEncryptedMessage(slices: QueueList<ByteArray>): Boolean {
        val iv = slices.pop()!!
        val cipher = slices.pop()!!
        val hmac = slices.pop()!!

        if (!Encryption.verifyHMAC(secretKey!!, cipher, hmac)) {
            return false
        }

        val message = Encryption.decryptWithAES(secretKey!!, iv, cipher) ?: return false
        protocolController.submitSessionMessageEvent(message)
        return true
    }

    fun sendMessage(message: ByteArray): Boolean {
        if (!isSessionStarted()) {
            return false
        }

        val iv = Encryption.generateBytes(ENCRYPTED_MESSAGE_IV_SIZE)
        val cipher = Encryption.encryptWithAES(secretKey!!, iv, message)!!
        val hmac = Encryption.computeHMAC(secretKey!!, cipher)
        protocolController.sendPacket(ENCRYPTED_MESSAGE_NAME, iv, cipher, hmac)
        return true
    }

    fun isEncryptedMessagePacket(name: ByteArray, slices: QueueList<ByteArray>): Boolean {
        return slices.size() == ENCRYPTED_MESSAGE_SLICE_COUNT && name.contentEquals(ENCRYPTED_MESSAGE_NAME)
    }

    fun isSessionStarted(): Boolean {
        return secretKey != null
    }

    fun resetSession() {
        secretKey = null
    }
}