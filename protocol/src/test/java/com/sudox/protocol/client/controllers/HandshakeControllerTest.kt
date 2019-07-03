package com.sudox.protocol.client.controllers

import com.nhaarman.mockitokotlin2.any
import com.sudox.encryption.ECDHSession
import com.sudox.encryption.Encryption
import com.sudox.protocol.client.ProtocolController
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.random.Random

private const val KEY_PAIR_POINTER = Long.MAX_VALUE
private val PUBLIC_KEY = Random.nextBytes(97)

@RunWith(PowerMockRunner::class)
@PrepareForTest(ProtocolController::class, Encryption::class)
class HandshakeControllerTest : Assert() {

    private lateinit var protocolController: ProtocolController
    private lateinit var handshakeController: HandshakeController

    @Before
    fun setUp() {
        protocolController = PowerMockito.mock(ProtocolController::class.java)
        handshakeController = HandshakeController(protocolController)

        PowerMockito.mockStatic(Encryption::class.java)
        PowerMockito
                .`when`(Encryption.startECDH())
                .thenReturn(ECDHSession(KEY_PAIR_POINTER, PUBLIC_KEY))
    }

    @Test
    fun testHandshakePacketIsSent() {
        handshakeController.start()

        assertNotNull(handshakeController.ecdhSession)
        Mockito.verify(protocolController).sendPacket(arrayOf(HANDSHAKE_PACKET_NAME, PUBLIC_KEY))
    }

    @Test
    fun testNotStartedButPacketReceived() {
        handshakeController.handlePacket(arrayOf(Any(), byteArrayOf(), byteArrayOf(), byteArrayOf()))
        Mockito.verify(protocolController, Mockito.never()).startSession(any())
    }

    @Test
    fun testInvalidParametersType() {
        handshakeController.apply {
            start()
            handlePacket(arrayOf(Any(), byteArrayOf(), Any(), Any()))
            handlePacket(arrayOf(Any(), byteArrayOf(), byteArrayOf(), Any()))
            handlePacket(arrayOf(Any(), Any(), Any(), Any()))
        }

        Mockito.verify(protocolController, Mockito.never()).startSession(any())
    }

    @Test
    fun testSignatureCheckingFailed() {
        val serverPublicKey = ByteArray(128) { -1 }
        val serverPublicKeySign = ByteArray(128) { -2 }
        val serverHmac = ByteArray(128) { -3 }

        PowerMockito.`when`(Encryption.verifySignature(any(), any())).thenReturn(false)

        handshakeController.apply {
            start()
            handlePacket(arrayOf(Any(), serverPublicKey, serverPublicKeySign, serverHmac))
        }

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.verifySignature(serverPublicKey, serverPublicKeySign)

        Mockito.verify(protocolController, Mockito.never()).startSession(any())
        Mockito.verify(protocolController).restartConnection()
    }

    @Test
    fun testSecretKeyCalculationFailed() {
        val serverPublicKey = ByteArray(128) { -1 }
        val serverPublicKeySign = ByteArray(128) { -2 }
        val serverHmac = ByteArray(128) { -3 }

        PowerMockito.`when`(Encryption.verifySignature(any(), any())).thenReturn(true)
        PowerMockito.`when`(Encryption.finishECDH(anyLong(), any())).thenReturn(null)

        handshakeController.apply {
            start()
            handlePacket(arrayOf(Any(), serverPublicKey, serverPublicKeySign, serverHmac))
        }

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.finishECDH(KEY_PAIR_POINTER, serverPublicKey)

        Mockito.verify(protocolController, Mockito.never()).startSession(any())
        Mockito.verify(protocolController).restartConnection()
    }

    @Test
    fun testHmacVerifyFailed() {
        val serverPublicKey = ByteArray(128) { -1 }
        val serverPublicKeySign = ByteArray(128) { -2 }
        val serverHmac = ByteArray(128) { -3 }
        val ecdhSecretKey = ByteArray(128) { -4 }
        val protocolSecretKey = ecdhSecretKey.copyOf(KEY_SIZE)

        PowerMockito.`when`(Encryption.verifySignature(any(), any())).thenReturn(true)
        PowerMockito.`when`(Encryption.finishECDH(anyLong(), any())).thenReturn(ecdhSecretKey)
        PowerMockito.`when`(Encryption.verifyHMAC(any(), any(), any())).thenReturn(false)

        handshakeController.apply {
            start()
            handlePacket(arrayOf(Any(), serverPublicKey, serverPublicKeySign, serverHmac))
        }

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.verifyHMAC(protocolSecretKey, OK, serverHmac)

        Mockito.verify(protocolController, Mockito.never()).startSession(any())
        Mockito.verify(protocolController).restartConnection()
    }

    @Test
    fun testSuccessHandshake() {
        val serverPublicKey = ByteArray(128) { -1 }
        val serverPublicKeySign = ByteArray(128) { -2 }
        val serverHmac = ByteArray(128) { -3 }
        val ecdhSecretKey = ByteArray(128) { -4 }
        val protocolSecretKey = ecdhSecretKey.copyOf(KEY_SIZE)

        PowerMockito.`when`(Encryption.verifySignature(any(), any())).thenReturn(true)
        PowerMockito.`when`(Encryption.finishECDH(anyLong(), any())).thenReturn(ecdhSecretKey)
        PowerMockito.`when`(Encryption.verifyHMAC(any(), any(), any())).thenReturn(true)

        handshakeController.apply {
            start()
            handlePacket(arrayOf(Any(), serverPublicKey, serverPublicKeySign, serverHmac))
        }

        Mockito.verify(protocolController).startSession(protocolSecretKey)
        assertNull(handshakeController.ecdhSession)
    }

    @Test
    fun testPacketChecking() {
        assertTrue(handshakeController.isPacket(HANDSHAKE_PACKET_NAME,
                arrayOfNulls<Any>(HANDSHAKE_PACKET_PARTS_COUNT + 1)))

        assertFalse(handshakeController.isPacket("not_handshake_packet",
                arrayOfNulls<Any>(HANDSHAKE_PACKET_PARTS_COUNT + 1)))

        assertFalse(handshakeController.isPacket(HANDSHAKE_PACKET_NAME,
                arrayOfNulls<Any>(0)))

        assertFalse(handshakeController.isPacket("not_handshake_packet",
                arrayOfNulls<Any>(0)))
    }

    @Test
    fun testNotStartedButTryingReset() {
        handshakeController.reset()
    }

    @Test
    fun testStartedAndTryingReset() {
        handshakeController.start()
        handshakeController.reset()

        PowerMockito.verifyStatic(Encryption::class.java)
        Encryption.closeECDH(KEY_PAIR_POINTER)
        assertNull(handshakeController.ecdhSession)
    }
}