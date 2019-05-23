package com.sudox.protocol.controllers

import com.sudox.cipher.Cipher
import com.sudox.protocol.ProtocolController
import java.util.LinkedList

class MessagesController(val protocolController: ProtocolController) {

    internal var secretKey: ByteArray? = null

    companion object {
        internal const val ENCRYPTED_MESSAGE_SLICE_COUNT = 3
        internal const val ENCRYPTED_MESSAGE_IV_SIZE = 16
    }

    internal fun handleIncomingMessage(slices: LinkedList<ByteArray>) {
        if (!handleEncryptedMessage(slices)) {
            protocolController.restartConnection()
        }
    }

    /**
     * Returns true if message successfully decrypted
     * Returns false if error thrown
     */
    internal fun handleEncryptedMessage(slices: LinkedList<ByteArray>): Boolean {
        if (slices.size != ENCRYPTED_MESSAGE_SLICE_COUNT) {
            return false
        }

        val iv = slices.remove()
        val cipher = slices.remove()
        val serverCipherHmac = slices.remove()

        // First step - calculating hmac using generated secret key
        val cipherHmac = Cipher.calculateHMAC(secretKey, cipher)

        // Second step - comparing server and own hmacs
        if (!Cipher.checkEqualsAllBytes(serverCipherHmac, cipherHmac)) {
            return false
        }

        // Third step - decrypting message
        val message = Cipher.decryptWithAES(secretKey, iv, cipher)
        protocolController.submitSessionMessageEvent(message)

        return true
    }

    /**
     * Returns false if message not sent
     * Returns true if message sent
     */
    internal fun sendEncryptedMessage(message: ByteArray): Boolean {
        if (!isSessionStarted()) {
            return false
        }

        val iv = Cipher.generateBytes(ENCRYPTED_MESSAGE_IV_SIZE)
        val cipher = Cipher.encryptWithAES(secretKey, iv, message)
        val cipherHmac = Cipher.calculateHMAC(secretKey, cipher)

        protocolController.sendPacket(iv, cipher, cipherHmac)
        return true
    }

    internal fun isSessionStarted(): Boolean {
        return secretKey != null
    }
}