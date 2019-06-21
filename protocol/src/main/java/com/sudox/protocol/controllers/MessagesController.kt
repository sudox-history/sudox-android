package com.sudox.protocol.controllers

import androidx.annotation.VisibleForTesting
import com.sudox.encryption.Encryption
import com.sudox.protocol.ProtocolController
import java.util.LinkedList

internal val ENCRYPTED_MESSAGE_NAME = byteArrayOf(0, 10, 0)
internal const val ENCRYPTED_MESSAGE_SLICE_COUNT = 3
internal const val ENCRYPTED_MESSAGE_IV_SIZE = 16

class MessagesController(val protocolController: ProtocolController) {

    private var secretKey: ByteArray? = null

    fun startSession(secretKey: ByteArray) {
        this.secretKey = secretKey
    }

    fun handleIncomingMessage(slices: LinkedList<ByteArray>) {
        if (!handleEncryptedMessage(slices)) {
            protocolController.restartConnection()
        }
    }

    /**
     * Returns true if message successfully decrypted
     * Returns false if error thrown
     */
    @VisibleForTesting
    fun handleEncryptedMessage(slices: LinkedList<ByteArray>): Boolean {
        val iv = slices.remove()
        val cipher = slices.remove()
        val serverCipherHmac = slices.remove()

        val cipherHmac = Encryption.computeHMAC(secretKey!!, cipher)
        if (!Encryption.checkEqualsAllBytes(serverCipherHmac, cipherHmac)) {
            return false
        }

        val message = Encryption.decryptWithAES(secretKey!!, iv, cipher) ?: return false
        protocolController.submitSessionMessageEvent(message)
        return true
    }

    /**
     * Returns false if message not sent
     * Returns true if message sent
     */
    fun sendEncryptedMessage(message: ByteArray): Boolean {
        if (!isSessionStarted()) {
            return false
        }

        val iv = Encryption.generateBytes(ENCRYPTED_MESSAGE_IV_SIZE)
        val cipher = Encryption.encryptWithAES(secretKey!!, iv, message)
        val cipherHmac = Encryption.computeHMAC(secretKey!!, cipher)
        protocolController.sendPacket(ENCRYPTED_MESSAGE_NAME, iv, cipher, cipherHmac)
        return true
    }

    fun isEncryptedMessagePacket(name: ByteArray, slices: LinkedList<ByteArray>): Boolean {
        return slices.size == ENCRYPTED_MESSAGE_SLICE_COUNT && name.contentEquals(ENCRYPTED_MESSAGE_NAME)
    }

    fun isSessionStarted(): Boolean {
        return secretKey != null
    }

    fun resetSession() {
        secretKey = null
    }
}