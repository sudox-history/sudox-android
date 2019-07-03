package com.sudox.protocol.client.controllers

import com.nhaarman.mockitokotlin2.any
import com.sudox.encryption.Encryption
import com.sudox.protocol.client.ProtocolController
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.random.Random

private val SECRET_KEY = Random.nextBytes(KEY_SIZE)

@RunWith(PowerMockRunner::class)
@PrepareForTest(ProtocolController::class, Encryption::class)
class MessagesControllerTest : Assert() {

    private lateinit var protocolController: ProtocolController
    private lateinit var messagesController: MessagesController

    @Before
    fun setUp() {
        protocolController = PowerMockito.mock(ProtocolController::class.java)
        messagesController = MessagesController(protocolController)

        PowerMockito.mockStatic(Encryption::class.java)
    }

    @Test
    fun testNotStartedButPacketReceived() {
        messagesController.handlePacket(arrayOf(Any(), byteArrayOf(), byteArrayOf(), byteArrayOf()))
        Mockito.verify(protocolController, Mockito.never()).submitMessageEvent(any())
    }

    @Test
    fun testInvalidParametersType() {
        messagesController.apply {
            start(SECRET_KEY)
            handlePacket(arrayOf(Any(), byteArrayOf(), Any(), Any()))
            handlePacket(arrayOf(Any(), byteArrayOf(), byteArrayOf(), Any()))
            handlePacket(arrayOf(Any(), Any(), Any(), Any()))
        }

        Mockito.verify(protocolController, Mockito.never()).submitMessageEvent(any())
    }

    @Test
    fun testHmacVerifyFailed() {
        val cipher = ByteArray(128) { -1 }
        val hmac = ByteArray(128) { -2 }
        val iv = ByteArray(128) { -3 }

        PowerMockito.`when`(Encryption.verifyHMAC(any(), any(), any())).thenReturn(false)

        messagesController.apply {
            start(SECRET_KEY)
            handlePacket(arrayOf(Any(), cipher, hmac, iv))
        }

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.verifyHMAC(SECRET_KEY, cipher, hmac)

        Mockito.verify(protocolController).restartConnection()
        Mockito.verify(protocolController, Mockito.never()).submitMessageEvent(any())
    }

    @Test
    fun testDecryptFailed() {
        val cipher = ByteArray(128) { -1 }
        val hmac = ByteArray(128) { -2 }
        val iv = ByteArray(128) { -3 }

        PowerMockito.`when`(Encryption.verifyHMAC(any(), any(), any())).thenReturn(true)
        PowerMockito.`when`(Encryption.decryptWithAES(any(), any(), any())).thenReturn(null)

        messagesController.apply {
            start(SECRET_KEY)
            handlePacket(arrayOf(Any(), cipher, hmac, iv))
        }

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.decryptWithAES(SECRET_KEY, iv, cipher)

        Mockito.verify(protocolController).restartConnection()
        Mockito.verify(protocolController, Mockito.never()).submitMessageEvent(any())
    }

    @Test
    fun testSuccess() {
        val cipher = ByteArray(128) { -1 }
        val hmac = ByteArray(128) { -2 }
        val iv = ByteArray(128) { -3 }
        val decryptedData = byteArrayOf(1, 1, 1, 1, 15, 15, 2)
        val valid = byteArrayOf(1, 1, 1, 1)

        PowerMockito.`when`(Encryption.verifyHMAC(any(), any(), any())).thenReturn(true)
        PowerMockito.`when`(Encryption.decryptWithAES(any(), any(), any())).thenReturn(decryptedData)

        messagesController.apply {
            start(SECRET_KEY)
            handlePacket(arrayOf(Any(), cipher, hmac, iv))
        }

        Mockito.verify(protocolController).submitMessageEvent(valid)
    }

    @Test
    fun testSendingWhenSessionNotStarted() {
        assertFalse(messagesController.send(byteArrayOf(1)))
        Mockito.verify(protocolController, Mockito.never()).sendPacket(any(), anyBoolean())
    }

    @Test
    fun testSending() {
        messagesController.start(SECRET_KEY)

        val message = "Hello World!".toByteArray()
        val ciphertext = "Encrypted data ...".toByteArray()

        val saltLength = Random.nextInt(SALT_LENGTH_RANGE_START, SALT_LENGTH_RANGE_END)
        val salt = Random.nextBytes(saltLength)
        val hmac = Random.nextBytes(48)
        val iv = Random.nextBytes(IV_SIZE)

        PowerMockito.`when`(Encryption.generateInt(anyInt(), anyInt())).thenReturn(saltLength)
        PowerMockito.`when`(Encryption.generateBytes(anyInt())).thenReturn(salt, iv)
        PowerMockito.`when`(Encryption.encryptWithAES(any(), any(), any())).thenReturn(ciphertext)
        PowerMockito.`when`(Encryption.computeHMAC(any(), any())).thenReturn(hmac)

        assertTrue(messagesController.send(message))
        Mockito.verify(protocolController).sendPacket(arrayOf(MESSAGES_PACKET_NAME, ciphertext, hmac, iv))
    }

    @Test
    fun testPacketChecking() {
        assertTrue(messagesController.isPacket(MESSAGES_PACKET_NAME,
                arrayOfNulls<Any>(MESSAGES_PARTS_COUNT + 1)))

        assertFalse(messagesController.isPacket("not_message_packet",
                arrayOfNulls<Any>(MESSAGES_PARTS_COUNT + 1)))

        assertFalse(messagesController.isPacket(MESSAGES_PACKET_NAME,
                arrayOfNulls<Any>(0)))

        assertFalse(messagesController.isPacket("not_message_packet",
                arrayOfNulls<Any>(0)))
    }

    @Test
    fun testSessionChecking() {
        assertFalse(messagesController.isSessionStarted())

        messagesController.start(SECRET_KEY)
        assertTrue(messagesController.isSessionStarted())

        messagesController.reset()
        assertFalse(messagesController.isSessionStarted())
    }
}