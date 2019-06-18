package com.sudox.protocol.controllers

import com.nhaarman.mockitokotlin2.any
import com.sudox.encryption.Encryption
import com.sudox.protocol.ProtocolController
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*
import kotlin.random.Random

@RunWith(PowerMockRunner::class)
@PrepareForTest(MessagesController::class, ProtocolController::class, Encryption::class)
class MessagesControllerTest : Assert() {

    private lateinit var messagesController: MessagesController
    private lateinit var protocolController: ProtocolController

    @Before
    fun setUp() {
        messagesController = PowerMockito.mock(MessagesController::class.java)
        protocolController = PowerMockito.mock(ProtocolController::class.java)

        MessagesController::class.java
                .getDeclaredField("protocolController")
                .apply { isAccessible = true }
                .set(messagesController, protocolController)

        Mockito.`when`(messagesController.handleIncomingMessage(any())).thenCallRealMethod()
    }

    @Test
    fun testHandleIncomingMessage_fail() {
        Mockito.`when`(messagesController.handleEncryptedMessage(any())).thenReturn(false)

        messagesController.handleIncomingMessage(LinkedList())
        Mockito.verify(protocolController).restartConnection()
    }

    @Test
    fun testHandleIncomingMessage_success() {
        Mockito.`when`(messagesController.handleEncryptedMessage(any())).thenReturn(true)

        messagesController.handleIncomingMessage(LinkedList())
        Mockito.verify(protocolController, Mockito.never()).restartConnection()
    }

    @Test
    fun testHandleEncryptedMessage_hmac_comparing() {
        val hmac = Random.nextBytes(128)
        val iv = Random.nextBytes(128)
        val cipher = Random.nextBytes(128)
        val serverCipherHmac = Random.nextBytes(128)
        val secretKey = Random.nextBytes(128)

        MessagesController::class.java
                .getDeclaredField("secretKey")
                .apply { isAccessible = true }
                .set(messagesController, secretKey)

        PowerMockito.mockStatic(Encryption::class.java)
        Mockito.`when`(Encryption.calculateHMAC(Mockito.any(), Mockito.any())).thenReturn(hmac)
        Mockito.`when`(Encryption.countNonEqualityBytes(any(), any())).thenCallRealMethod()
        Mockito.`when`(Encryption.checkEqualsAllBytes(any(), any())).thenCallRealMethod()
        Mockito.`when`(messagesController.handleEncryptedMessage(any())).thenCallRealMethod()

        val slices = LinkedList<ByteArray>()

        slices.add(iv)
        slices.add(cipher)
        slices.add(serverCipherHmac)

        assertFalse(messagesController.handleEncryptedMessage(slices))
        Mockito.verify(protocolController, Mockito.never()).submitSessionMessageEvent(any())

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.calculateHMAC(secretKey, cipher)
    }

    @Test
    fun testHandleEncryptedMessage_success() {
        val message = Random.nextBytes(128)
        val iv = Random.nextBytes(128)
        val cipher = Random.nextBytes(128)
        val serverCipherHmac = Random.nextBytes(128)
        val secretKey = Random.nextBytes(128)

        MessagesController::class.java
                .getDeclaredField("secretKey")
                .apply { isAccessible = true }
                .set(messagesController, secretKey)

        PowerMockito.mockStatic(Encryption::class.java)
        Mockito.`when`(Encryption.calculateHMAC(Mockito.any(), Mockito.any())).thenReturn(serverCipherHmac)
        Mockito.`when`(Encryption.countNonEqualityBytes(any(), any())).thenCallRealMethod()
        Mockito.`when`(Encryption.checkEqualsAllBytes(any(), any())).thenCallRealMethod()
        Mockito.`when`(Encryption.decryptWithAES(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(message)
        Mockito.`when`(messagesController.handleEncryptedMessage(any())).thenCallRealMethod()

        val slices = LinkedList<ByteArray>()

        slices.add(iv)
        slices.add(cipher)
        slices.add(serverCipherHmac)

        assertTrue(messagesController.handleEncryptedMessage(slices))
        Mockito.verify(protocolController).submitSessionMessageEvent(message)

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.decryptWithAES(secretKey, iv, cipher)
    }

    @Test
    fun testSendEncryptedMessage_session_not_started() {
        val message = Random.nextBytes(128)

        Mockito.`when`(messagesController.isSessionStarted()).thenReturn(false)
        Mockito.`when`(messagesController.sendEncryptedMessage(any())).thenCallRealMethod()

        assertFalse(messagesController.sendEncryptedMessage(message))
        Mockito.verify(protocolController, Mockito.never()).sendPacket(any())
    }

    @Test
    fun testSendEncryptedMessage_success() {
        val iv = Random.nextBytes(128)
        val message = Random.nextBytes(128)
        val secretKey = Random.nextBytes(128)
        val cipher = Random.nextBytes(128)
        val cipherHmac = Random.nextBytes(128)

        MessagesController::class.java
                .getDeclaredField("secretKey")
                .apply { isAccessible = true }
                .set(messagesController, secretKey)

        PowerMockito.mockStatic(Encryption::class.java)
        Mockito.`when`(Encryption.generateBytes(any())).thenReturn(iv)
        Mockito.`when`(Encryption.encryptWithAES(any(), any(), any())).thenReturn(cipher)
        Mockito.`when`(Encryption.calculateHMAC(secretKey, cipher)).thenReturn(cipherHmac)
        Mockito.`when`(messagesController.sendEncryptedMessage(any())).thenCallRealMethod()
        Mockito.`when`(messagesController.isSessionStarted()).thenReturn(true)

        assertTrue(messagesController.sendEncryptedMessage(message))
        Mockito.verify(protocolController).sendPacket(ENCRYPTED_MESSAGE_NAME, iv, cipher, cipherHmac)

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.generateBytes(ENCRYPTED_MESSAGE_IV_SIZE)

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.encryptWithAES(secretKey, iv, message)

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.calculateHMAC(secretKey, cipher)
    }

    @Test
    fun testIsSessionValid() {
        Mockito.`when`(messagesController.isSessionStarted()).thenCallRealMethod()
        Mockito.`when`(messagesController.resetSession()).thenCallRealMethod()
        assertFalse(messagesController.isSessionStarted())

        MessagesController::class.java
                .getDeclaredField("secretKey")
                .apply { isAccessible = true }
                .set(messagesController, ByteArray(128))

        assertTrue(messagesController.isSessionStarted())
        messagesController.resetSession()
        assertFalse(messagesController.isSessionStarted())
    }

    @Test
    fun testIsEncryptedMessagePacket() {
        Mockito.`when`(messagesController.isEncryptedMessagePacket(any(), any())).thenCallRealMethod()

        val slices = LinkedList<ByteArray>()
        slices.add(ByteArray(3))
        slices.add(ByteArray(3))
        slices.add(ByteArray(3))

        assertTrue(messagesController.isEncryptedMessagePacket(ENCRYPTED_MESSAGE_NAME, slices))
        assertFalse(messagesController.isEncryptedMessagePacket("H".toByteArray(), slices))
        assertFalse(messagesController.isEncryptedMessagePacket("H".toByteArray(),  LinkedList()))
        assertFalse(messagesController.isEncryptedMessagePacket(ENCRYPTED_MESSAGE_NAME,  LinkedList()))
    }
}