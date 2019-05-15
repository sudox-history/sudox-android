package com.sudox.protocol.controllers

import com.sudox.protocol.ProtocolController
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*
import kotlin.random.Random

@RunWith(PowerMockRunner::class)
@PrepareForTest(HandshakeController::class, ProtocolController::class)
class HandshakeControllerTest : Assert() {

    private lateinit var handshakeController: HandshakeController
    private lateinit var protocolController: ProtocolController
    private lateinit var privateKey: ByteArray
    private lateinit var publicKey: ByteArray
    private lateinit var slices: LinkedList<ByteArray>

    @Before
    fun setUp() {
        handshakeController = PowerMockito.mock(HandshakeController::class.java)
        protocolController = PowerMockito.mock(ProtocolController::class.java)
        publicKey = Random.nextBytes(128)
        privateKey = Random.nextBytes(128)
        slices = LinkedList()

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
        Mockito.`when`(handshakeController.isHandshakeSucceed()).thenCallRealMethod()
        Mockito.`when`(handshakeController.handleIncomingMessage(slices)).thenCallRealMethod()
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
        assertFalse(handshakeController.isHandshakeSucceed())
    }

    @Test
    fun testHandleIncomingMessage_when_key_not_waiting() {
        handshakeController.handleIncomingMessage(slices)

        Mockito.verify(handshakeController, Mockito.never()).handlePublicKeyMessage(slices)
        Mockito.verify(protocolController).restartConnection()
    }

    @Test
    fun testHandleIncomingMessage_when_key_waiting() {
        HandshakeController::class.java
                .getDeclaredField("handshakeStatus")
                .apply { isAccessible = true }
                .set(handshakeController, HandshakeStatus.WAIT_SERVER_PUBLIC_KEY)

        Mockito.`when`(handshakeController.handleIncomingMessage(slices)).thenCallRealMethod()
        Mockito.`when`(handshakeController.handlePublicKeyMessage(slices)).thenReturn(true)

        // Testing when secret key not generated
        handshakeController.handleIncomingMessage(slices)

        Mockito.verify(handshakeController).handlePublicKeyMessage(slices)
        Mockito.verify(protocolController, Mockito.never()).restartConnection()
    }
}