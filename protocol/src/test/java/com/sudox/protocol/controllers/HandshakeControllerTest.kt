package com.sudox.protocol.controllers

import com.nhaarman.mockitokotlin2.any
import com.sudox.cipher.Cipher
import com.sudox.protocol.ProtocolController
import com.sudox.protocol.controllers.HandshakeController.Companion.HMAC_VALIDATION_WORD
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
@PrepareForTest(HandshakeController::class, ProtocolController::class, Cipher::class)
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
        Mockito.`when`(handshakeController.handleIncomingMessage(any())).thenCallRealMethod()
    }

    @Test
    fun startHandshake() {
        handshakeController.startHandshake()

        Mockito.verify(handshakeController).generateKeysPair()
        Mockito.verify(protocolController).sendPacket(publicKey)

        val handshakeStatus = HandshakeController::class.java
                .getDeclaredField("handshakeStatus")
                .apply { isAccessible = true }
                .get(handshakeController) as Int

        assertEquals(HandshakeStatus.WAIT_SERVER_PUBLIC_KEY, handshakeStatus)
    }

    @Test
    fun testHandleIncomingMessage_when_key_not_waiting() {
        handshakeController.handleIncomingMessage(LinkedList())

        Mockito.verify(handshakeController, Mockito.never()).handlePublicKeyMessage(any())
        Mockito.verify(protocolController).restartConnection()
    }

    @Test
    fun testHandleIncomingMessage_when_key_waiting() {
        HandshakeController::class.java
                .getDeclaredField("handshakeStatus")
                .apply { isAccessible = true }
                .set(handshakeController, HandshakeStatus.WAIT_SERVER_PUBLIC_KEY)

        Mockito.`when`(handshakeController.handlePublicKeyMessage(any())).thenReturn(true)

        // Testing when secret key not generated
        handshakeController.handleIncomingMessage(LinkedList())

        Mockito.verify(protocolController, Mockito.never()).restartConnection()
        Mockito.verify(handshakeController).handlePublicKeyMessage(any())
    }

    @Test
    fun testHandlePublicKeyMessage_not_valid_slices_count() {
        val secretKey = ByteArray(0)
        val slices = LinkedList<ByteArray>()

        PowerMockito.mockStatic(Cipher::class.java)
        Mockito.`when`(Cipher.calculateSecretKey(Mockito.any(), Mockito.any())).thenReturn(secretKey)
        Mockito.`when`(handshakeController.handlePublicKeyMessage(any())).thenCallRealMethod()

        slices.add(ByteArray(128))
        slices.add(ByteArray(128))

        assertFalse(handshakeController.handlePublicKeyMessage(slices))
        Mockito.verify(protocolController, Mockito.never()).startEncryptedSession(secretKey)

        slices.clear()
        slices.add(ByteArray(128))
        slices.add(ByteArray(128))
        slices.add(ByteArray(128))
        slices.add(ByteArray(128))

        assertFalse(handshakeController.handlePublicKeyMessage(slices))
        Mockito.verify(protocolController, Mockito.never()).startEncryptedSession(secretKey)

        val handshakeStatus = HandshakeController::class.java
                .getDeclaredField("handshakeStatus")
                .apply { isAccessible = true }
                .get(handshakeController) as Int

        assertNotEquals(HandshakeStatus.SUCCESS, handshakeStatus)
    }

    @Test
    fun testHandlePublicKeyMessage_signature_checking() {
        val secretKey = ByteArray(0)
        val slices = LinkedList<ByteArray>()

        val publicKey = ByteArray(128) { -1 }
        val publicKeySign = ByteArray(128) { -2 }
        val okHmac = ByteArray(128) { -3 }

        PowerMockito.mockStatic(Cipher::class.java)
        Mockito.`when`(Cipher.verifyMessageWithECDSA(Mockito.any(), Mockito.any())).thenReturn(false)
        Mockito.`when`(Cipher.checkEqualsAllBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(Cipher.countNonEqualityBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(Cipher.calculateSecretKey(Mockito.any(), Mockito.any())).thenReturn(secretKey)
        Mockito.`when`(handshakeController.handlePublicKeyMessage(slices)).thenCallRealMethod()

        slices.add(publicKey)
        slices.add(publicKeySign)
        slices.add(okHmac)

        handshakeController.generateKeysPair()
        assertFalse(handshakeController.handlePublicKeyMessage(slices))
        Mockito.verify(protocolController, Mockito.never()).startEncryptedSession(secretKey)

        val handshakeStatus = HandshakeController::class.java
                .getDeclaredField("handshakeStatus")
                .apply { isAccessible = true }
                .get(handshakeController) as Int

        assertNotEquals(HandshakeStatus.SUCCESS, handshakeStatus)

        PowerMockito.verifyStatic(Cipher::class.java)
        Cipher.verifyMessageWithECDSA(publicKey, publicKeySign)
    }

    @Test
    fun testHandlePublicKeyMessage_calculating_secret_key() {
        val secretKey = ByteArray(0)
        val slices = LinkedList<ByteArray>()

        val publicKey = ByteArray(128) { -1 }
        val publicKeySign = ByteArray(128) { -2 }
        val okHmac = ByteArray(128) { -3 }

        PowerMockito.mockStatic(Cipher::class.java)
        Mockito.`when`(Cipher.verifyMessageWithECDSA(Mockito.any(), Mockito.any())).thenReturn(true)
        Mockito.`when`(Cipher.calculateSecretKey(Mockito.any(), Mockito.any())).thenReturn(secretKey)
        Mockito.`when`(Cipher.checkEqualsAllBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(Cipher.countNonEqualityBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(handshakeController.handlePublicKeyMessage(slices)).thenCallRealMethod()

        slices.add(publicKey)
        slices.add(publicKeySign)
        slices.add(okHmac)

        handshakeController.generateKeysPair()
        assertFalse(handshakeController.handlePublicKeyMessage(slices))
        Mockito.verify(protocolController, Mockito.never()).startEncryptedSession(secretKey)

        val handshakeStatus = HandshakeController::class.java
                .getDeclaredField("handshakeStatus")
                .apply { isAccessible = true }
                .get(handshakeController) as Int

        assertNotEquals(HandshakeStatus.SUCCESS, handshakeStatus)

        PowerMockito.verifyStatic(Cipher::class.java)
        Cipher.calculateSecretKey(privateKey, publicKey)
    }

    @Test
    fun testHandlePublicKeyMessage_comparing_hmac() {
        val secretKey = Random.nextBytes(128)
        val slices = LinkedList<ByteArray>()

        val publicKey = ByteArray(128) { -1 }
        val publicKeySign = ByteArray(128) { -2 }
        val okHmac = ByteArray(128) { -3 }

        PowerMockito.mockStatic(Cipher::class.java)
        Mockito.`when`(Cipher.verifyMessageWithECDSA(Mockito.any(), Mockito.any())).thenReturn(true)
        Mockito.`when`(Cipher.calculateSecretKey(Mockito.any(), Mockito.any())).thenReturn(secretKey)
        Mockito.`when`(Cipher.calculateHMAC(Mockito.any(), Mockito.any())).thenReturn(Random.nextBytes(128))
        Mockito.`when`(Cipher.checkEqualsAllBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(Cipher.countNonEqualityBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(handshakeController.handlePublicKeyMessage(slices)).thenCallRealMethod()

        slices.add(publicKey)
        slices.add(publicKeySign)
        slices.add(okHmac)

        assertFalse(handshakeController.handlePublicKeyMessage(slices))

        val handshakeStatus = HandshakeController::class.java
                .getDeclaredField("handshakeStatus")
                .apply { isAccessible = true }
                .get(handshakeController) as Int

        assertNotEquals(HandshakeStatus.SUCCESS, handshakeStatus)
        Mockito.verify(protocolController, Mockito.never()).startEncryptedSession(secretKey)

        PowerMockito.verifyStatic(Cipher::class.java)
        Cipher.calculateHMAC(secretKey, HMAC_VALIDATION_WORD)
    }

    @Test
    fun testHandlePublicKeyMessage_success() {
        val secretKey = Random.nextBytes(128)
        val slices = LinkedList<ByteArray>()

        PowerMockito.mockStatic(Cipher::class.java)
        Mockito.`when`(Cipher.verifyMessageWithECDSA(Mockito.any(), Mockito.any())).thenReturn(true)
        Mockito.`when`(Cipher.calculateSecretKey(Mockito.any(), Mockito.any())).thenReturn(secretKey)
        Mockito.`when`(Cipher.calculateHMAC(Mockito.any(), Mockito.any())).thenReturn(ByteArray(128))
        Mockito.`when`(Cipher.checkEqualsAllBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(Cipher.countNonEqualityBytes(Mockito.any(), Mockito.any())).thenCallRealMethod()
        Mockito.`when`(handshakeController.handlePublicKeyMessage(slices)).thenCallRealMethod()

        slices.add(ByteArray(128))
        slices.add(ByteArray(128))
        slices.add(ByteArray(128))

        assertTrue(handshakeController.handlePublicKeyMessage(slices))

        val handshakeStatus = HandshakeController::class.java
                .getDeclaredField("handshakeStatus")
                .apply { isAccessible = true }
                .get(handshakeController) as Int

        assertEquals(HandshakeStatus.SUCCESS, handshakeStatus)
        Mockito.verify(protocolController).startEncryptedSession(secretKey)
    }

    @Test
    fun testResetKeys() {
        Mockito.`when`(handshakeController.resetKeys()).thenCallRealMethod()

        handshakeController.generateKeysPair()
        handshakeController.resetKeys()

        val publicKey = HandshakeController::class.java
                .getDeclaredField("ownPublicKey")
                .apply { isAccessible = true }
                .get(handshakeController)

        val privateKey = HandshakeController::class.java
                .getDeclaredField("ownPrivateKey")
                .apply { isAccessible = true }
                .get(handshakeController)

        assertNull(publicKey)
        assertNull(privateKey)
    }

    @Test
    fun testResetHandshake() {
        HandshakeController::class.java
                .getDeclaredField("handshakeStatus")
                .apply { isAccessible = true }
                .set(handshakeController, HandshakeStatus.WAIT_SERVER_PUBLIC_KEY)

        Mockito.`when`(handshakeController.resetKeys()).thenCallRealMethod()

        handshakeController.generateKeysPair()
        handshakeController.resetHandshake()

        val publicKey = HandshakeController::class.java
                .getDeclaredField("ownPublicKey")
                .apply { isAccessible = true }
                .get(handshakeController)

        val privateKey = HandshakeController::class.java
                .getDeclaredField("ownPrivateKey")
                .apply { isAccessible = true }
                .get(handshakeController)

        val handshakeStatus = HandshakeController::class.java
                .getDeclaredField("handshakeStatus")
                .apply { isAccessible = true }
                .get(handshakeController) as Int

        assertEquals(HandshakeStatus.NOT_STARTED, handshakeStatus)
        assertNull(publicKey)
        assertNull(privateKey)
    }

    @Test
    fun testGenerateKeysPair() {
        Mockito.`when`(handshakeController.generateKeysPair()).thenCallRealMethod()

        val generatedPublicKey = Random.nextBytes(1024)
        val generatedPrivateKey = Random.nextBytes(1024)

        PowerMockito.mockStatic(Cipher::class.java)
        Mockito.`when`(Cipher.generateKeysPair()).thenReturn(1)
        Mockito.`when`(Cipher.getPublicKey(1)).thenReturn(generatedPublicKey)
        Mockito.`when`(Cipher.getPrivateKey(1)).thenReturn(generatedPrivateKey)

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

        PowerMockito.verifyStatic(Cipher::class.java)
        Cipher.removeKeysPair(1)
    }
}