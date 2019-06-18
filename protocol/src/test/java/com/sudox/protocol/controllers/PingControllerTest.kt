package com.sudox.protocol.controllers

import com.nhaarman.mockitokotlin2.any
import com.sudox.protocol.ProtocolController
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(PingController::class, ProtocolController::class)
class PingControllerTest : Assert() {
    private lateinit var protocolController: ProtocolController
    private lateinit var pingController: PingController

    @Before
    fun setUp() {
        pingController = PowerMockito.mock(PingController::class.java)
        protocolController = PowerMockito.mock(ProtocolController::class.java)

        PingController::class.java
                .getDeclaredField("protocolController")
                .apply { isAccessible = true }
                .set(pingController, protocolController)

        Mockito.`when`(pingController.startPingCycle()).thenCallRealMethod()
        Mockito.`when`(pingController.handlePing()).thenCallRealMethod()
        Mockito.`when`(pingController.checkPing()).thenCallRealMethod()
        Mockito.`when`(pingController.sendPing()).thenCallRealMethod()
        Mockito.`when`(pingController.isPingPacket(any())).thenCallRealMethod()
    }

    @Test
    fun testIsPingPacket() {
        assertTrue(pingController.isPingPacket(PING_PACKET_NAME))
        assertFalse(pingController.isPingPacket("H".toByteArray()))
    }

    @Test
    fun testPing_success() {
        pingController.apply {
            startPingCycle()
            sendPing()
            handlePing()
            checkPing()
        }

        Mockito.verify(protocolController, Mockito.never()).restartConnection()

        pingController.apply {
            sendPing()
            handlePing()
            checkPing()
        }

        Mockito.verify(protocolController, Mockito.never()).restartConnection()

        pingController.apply {
            handlePing()
            checkPing()
            sendPing()
        }

        Mockito.verify(protocolController, Mockito.never()).restartConnection()
    }

    @Test
    fun testPing_error() {
        pingController.apply {
            startPingCycle()
            sendPing()
            checkPing()
        }

        Mockito.verify(protocolController).restartConnection()
    }
}