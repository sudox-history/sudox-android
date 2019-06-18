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
@PrepareForTest(HandshakeController::class, ProtocolController::class, Encryption::class)
class HandshakeControllerTest : Assert() {
    private lateinit var handshakeController: HandshakeController
    private lateinit var protocolController: ProtocolController
    private lateinit var privateKey: ByteArray
    private lateinit var publicKey: ByteArray

    @Before
    fun setUp() {
        handshakeController = PowerMockito.mock(HandshakeController::class.java)
        protocolController = PowerMockito.mock(ProtocolController::class.java)
        publicKey = Random.nextBytes(128)
        privateKey = Random.nextBytes(128)

        HandshakeController::class.java
                .getDeclaredField("protocolController")
                .apply { isAccessible = true }
                .set(handshakeController, protocolController)

        Mockito.`when`(handshakeController.generateKeysPair()).thenAnswer {
            HandshakeController::class.java
                    .getDeclaredField("ownPublicKey")
                    .apply { isAccessible = true }
                    .set(handshakeController, publicKey)

            HandshakeController::class.java
                    .getDeclaredField("ownPrivateKey")
                    .apply { isAccessible = true }
                    .set(handshakeController, privateKey)
        }

        Mockito.`when`(handshakeController.startHandshake()).thenCallRealMethod()
        Mockito.`when`(handshakeController.resetHandshake()).thenCallRealMethod()
        Mockito.`when`(handshakeController.handleIncomingPacket(any())).thenCallRealMethod()
    }

    @Test
    fun testHandlePublicKeyMessage_signature_checking() {
        val slices = LinkedList<ByteArray>()

        val publicKey = ByteArray(128) { -1 }
        val publicKeySign = ByteArray(128) { -2 }
        val okHmac = ByteArray(128) { -3 }

        PowerMockito.mockStatic(Encryption::class.java)
        Mockito.`when`(Encryption.verifyMessageWithECDSA(Mockito.any(), Mockito.any())).thenReturn(false)
        Mockito.`when`(Encryption.checkEqualsAllBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(Encryption.countNonEqualityBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(Encryption.calculateSecretKey(Mockito.any(), Mockito.any())).thenReturn(null)
        Mockito.`when`(handshakeController.handleHandshakePacket(slices)).thenCallRealMethod()

        slices.apply {
            add(publicKey)
            add(publicKeySign)
            add(okHmac)
        }

        handshakeController.generateKeysPair()
        assertFalse(handshakeController.handleHandshakePacket(slices))
        Mockito.verify(protocolController, Mockito.never()).startEncryptedSession(any())
    }

    @Test
    fun testHandlePublicKeyMessage_calculating_secret_key() {
        val slices = LinkedList<ByteArray>()

        val publicKey = ByteArray(128) { -1 }
        val publicKeySign = ByteArray(128) { -2 }
        val okHmac = ByteArray(128) { -3 }

        PowerMockito.mockStatic(Encryption::class.java)
        Mockito.`when`(Encryption.verifyMessageWithECDSA(Mockito.any(), Mockito.any())).thenReturn(true)
        Mockito.`when`(Encryption.calculateSecretKey(Mockito.any(), Mockito.any())).thenReturn(null)
        Mockito.`when`(Encryption.checkEqualsAllBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(Encryption.countNonEqualityBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(handshakeController.handleHandshakePacket(slices)).thenCallRealMethod()

        slices.apply {
            add(publicKey)
            add(publicKeySign)
            add(okHmac)
        }

        handshakeController.generateKeysPair()
        assertFalse(handshakeController.handleHandshakePacket(slices))
        Mockito.verify(protocolController, Mockito.never()).startEncryptedSession(any())
    }

    @Test
    fun testHandlePublicKeyMessage_comparing_hmac() {
        val secretKey = Random.nextBytes(128)
        val slices = LinkedList<ByteArray>()

        val publicKey = ByteArray(128) { -1 }
        val publicKeySign = ByteArray(128) { -2 }
        val okHmac = ByteArray(128) { -3 }

        PowerMockito.mockStatic(Encryption::class.java)
        Mockito.`when`(Encryption.verifyMessageWithECDSA(Mockito.any(), Mockito.any())).thenReturn(true)
        Mockito.`when`(Encryption.calculateSecretKey(Mockito.any(), Mockito.any())).thenReturn(secretKey)
        Mockito.`when`(Encryption.calculateHMAC(Mockito.any(), Mockito.any())).thenReturn(Random.nextBytes(128))
        Mockito.`when`(Encryption.checkEqualsAllBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(Encryption.countNonEqualityBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(handshakeController.handleHandshakePacket(slices)).thenCallRealMethod()

        slices.apply {
            add(publicKey)
            add(publicKeySign)
            add(okHmac)
        }

        handshakeController.generateKeysPair()
        assertFalse(handshakeController.handleHandshakePacket(slices))
        Mockito.verify(protocolController, Mockito.never()).startEncryptedSession(secretKey)
    }

    @Test
    fun testHandlePublicKeyMessage_success() {
        val secretKey = Random.nextBytes(128)
        val slices = LinkedList<ByteArray>()

        PowerMockito.mockStatic(Encryption::class.java)
        Mockito.`when`(Encryption.verifyMessageWithECDSA(Mockito.any(), Mockito.any())).thenReturn(true)
        Mockito.`when`(Encryption.calculateSecretKey(Mockito.any(), Mockito.any())).thenReturn(secretKey)
        Mockito.`when`(Encryption.calculateHMAC(Mockito.any(), Mockito.any())).thenReturn(ByteArray(128))
        Mockito.`when`(Encryption.checkEqualsAllBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(Encryption.countNonEqualityBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(handshakeController.handleHandshakePacket(slices)).thenCallRealMethod()

        for (i in 1..3){
            slices.add(ByteArray(128))
        }

        handshakeController.generateKeysPair()
        assertTrue(handshakeController.handleHandshakePacket(slices))
        Mockito.verify(protocolController).startEncryptedSession(secretKey)
    }

    @Test
    fun testGenerateKeysPair() {
        Mockito.`when`(handshakeController.generateKeysPair()).thenCallRealMethod()

        val generatedPublicKey = Random.nextBytes(1024)
        val generatedPrivateKey = Random.nextBytes(1024)

        PowerMockito.mockStatic(Encryption::class.java)
        Mockito.`when`(Encryption.generateKeysPair()).thenReturn(1)
        Mockito.`when`(Encryption.getPublicKey(1)).thenReturn(generatedPublicKey)
        Mockito.`when`(Encryption.getPrivateKey(1)).thenReturn(generatedPrivateKey)

        handshakeController.generateKeysPair()

        val publicKey = HandshakeController::class.java
                .getDeclaredField("ownPublicKey")
                .apply { isAccessible = true }
                .get(handshakeController)

        val privateKey = HandshakeController::class.java
                .getDeclaredField("ownPrivateKey")
                .apply { isAccessible = true }
                .get(handshakeController)

        assertEquals(generatedPublicKey, publicKey)
        assertEquals(generatedPrivateKey, privateKey)
    }

    @Test
    fun testIsHandshakePacket() {
        Mockito.`when`(handshakeController.isHandshakePacket(any(), any())).thenCallRealMethod()

        val slices = LinkedList<ByteArray>()
        for (i in 1..3){
            slices.add(ByteArray(3))
        }

        assertTrue(handshakeController.isHandshakePacket(HANDSHAKE_MESSAGE_NAME, slices))
        assertFalse(handshakeController.isHandshakePacket("H".toByteArray(), slices))
        assertFalse(handshakeController.isHandshakePacket("H".toByteArray(), LinkedList()))
        assertFalse(handshakeController.isHandshakePacket(HANDSHAKE_MESSAGE_NAME, LinkedList()))
    }
}