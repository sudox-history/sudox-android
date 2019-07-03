package com.sudox.protocol.client.controllers

import android.os.Handler
import android.os.SystemClock
import com.sudox.protocol.client.ProtocolController
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(ProtocolController::class, PingController::class, SystemClock::class)
class PingControllerTest : Assert() {

    private lateinit var handler: Handler
    private lateinit var protocolController: ProtocolController
    private lateinit var pingController: PingController

    @Before
    fun setUp() {
        handler = Mockito.mock(Handler::class.java)
        protocolController = PowerMockito.mock(ProtocolController::class.java)
        pingController = PowerMockito.spy(PingController(protocolController))

        PowerMockito.mockStatic(SystemClock::class.java)
        PowerMockito.`when`(SystemClock.uptimeMillis()).thenReturn(0)
        Mockito.`when`(protocolController.handler).thenReturn(handler)
    }

    @Test
    fun testIdle() {
        pingController.start()
        Mockito.verify(pingController).scheduleSendTask()

        for (i in 1..10) {
            pingController.send()
            Mockito.clearInvocations(pingController)

            pingController.handlePacket()
            pingController.check()

            Mockito.verify(protocolController, Mockito.never()).restartConnection()
            Mockito.verify(pingController, Mockito.never()).sendPacket()
        }
    }

    @Test
    fun testServerNotAnswered() {
        pingController.start()
        Mockito.verify(pingController).scheduleSendTask()

        pingController.send()
        pingController.check()

        Mockito.verify(protocolController).restartConnection()
    }

    @Test
    fun testPacketDetecting() {
        assertFalse(pingController.isPacket("not packet"))
        assertTrue(pingController.isPacket(PING_PACKET_NAME))
    }
}