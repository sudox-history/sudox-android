package com.sudox.protocol.controllers

import android.os.Handler
import android.os.SystemClock
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.sudox.protocol.ProtocolController
import com.sudox.protocol.controllers.PingController.Companion.PING_CHECK_TASK_ID
import com.sudox.protocol.controllers.PingController.Companion.PING_SEND_TASK_ID
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*

@RunWith(PowerMockRunner::class)
@PrepareForTest(PingController::class, ProtocolController::class, SystemClock::class)
class PingControllerTest : Assert() {

    private lateinit var pingController: PingController
    private lateinit var protocolController: ProtocolController
    private lateinit var threadHandler: Handler

    @Before
    fun setUp() {
        pingController = PowerMockito.mock(PingController::class.java)
        protocolController = PowerMockito.mock(ProtocolController::class.java)
        threadHandler = Mockito.mock(Handler::class.java)

        PingController::class.java
                .getDeclaredField("protocolController")
                .apply { isAccessible = true }
                .set(pingController, protocolController)

        Mockito.`when`(protocolController.threadHandler).thenReturn(threadHandler)
    }

    @Test
    fun testHandlePing_ping_requested() {
        Mockito.`when`(pingController.handlePing()).thenCallRealMethod()

        PingController::class.java
                .getDeclaredField("pingRequestedOrDelivered")
                .apply { isAccessible = true }
                .set(pingController, true)

        pingController.handlePing()

        val pingRequested = PingController::class.java
                .getDeclaredField("pingRequestedOrDelivered")
                .apply { isAccessible = true }
                .get(pingController) as Boolean

        Mockito.verify(pingController).sendPingPacket()
        assertTrue(pingRequested)
    }

    @Test
    fun testHandlePing_ping_not_requested() {
        Mockito.`when`(pingController.handlePing()).thenCallRealMethod()

        pingController.handlePing()

        val pingRequested = PingController::class.java
                .getDeclaredField("pingRequestedOrDelivered")
                .apply { isAccessible = true }
                .get(pingController) as Boolean

        Mockito.verify(pingController, Mockito.never()).sendPingPacket()
        assertTrue(pingRequested)
    }

    @Test
    fun testSchedulePingSendTask() {
        PowerMockito.mockStatic(SystemClock::class.java)
        Mockito.`when`(pingController.schedulePingSendTask()).thenCallRealMethod()
        Mockito.`when`(SystemClock.uptimeMillis()).thenReturn(0)

        pingController.schedulePingSendTask()
        Mockito.verify(threadHandler).removeCallbacksAndMessages(PING_SEND_TASK_ID)
        Mockito.verify(threadHandler).removeCallbacksAndMessages(PING_CHECK_TASK_ID)
        Mockito.verify(threadHandler).postAtTime(any(), eq(PING_SEND_TASK_ID), eq(PingController.PING_SEND_INTERVAL_IN_MILLIS))
    }

    @Test
    fun testSchedulePingCheckTask() {
        PowerMockito.mockStatic(SystemClock::class.java)
        Mockito.`when`(pingController.schedulePingCheckTask()).thenCallRealMethod()
        Mockito.`when`(SystemClock.uptimeMillis()).thenReturn(0)

        pingController.schedulePingCheckTask()
        Mockito.verify(threadHandler).postAtTime(any(), eq(PING_CHECK_TASK_ID), eq(PingController.PING_CHECK_INTERVAL_IN_MILLIS))
    }

    @Test
    fun testIsPingPacket() {
        val slices = LinkedList<ByteArray>()

        Mockito.`when`(pingController.isPingPacket(any())).thenCallRealMethod()

        assertFalse(pingController.isPingPacket(slices))

        slices.add(ByteArray(128))
        assertFalse(pingController.isPingPacket(slices))

        slices.clear()
        slices.add(ByteArray(128))
        slices.add(ByteArray(128))
        assertFalse(pingController.isPingPacket(slices))

        slices.clear()
        slices.add(PingController.PING_PACKET_NAME)
        assertTrue(pingController.isPingPacket(slices))
    }

    @Test
    fun testSendPing() {
        Mockito.`when`(pingController.sendPing()).thenCallRealMethod()

        pingController.sendPing()

        val pingRequested = PingController::class.java
                .getDeclaredField("pingRequestedOrDelivered")
                .apply { isAccessible = true }
                .get(pingController) as Boolean

        Mockito.verify(pingController).sendPingPacket()
        Mockito.verify(pingController).schedulePingCheckTask()
        assertFalse(pingRequested)
    }

    @Test
    fun testCheckPing() {
        Mockito.`when`(pingController.checkPing()).thenCallRealMethod()

        pingController.checkPing()

        PingController::class.java
                .getDeclaredField("pingRequestedOrDelivered")
                .apply { isAccessible = true }
                .set(pingController, true)

        Mockito.verify(protocolController).restartConnection()
        Mockito.reset(protocolController)

        pingController.checkPing()

        PingController::class.java
                .getDeclaredField("pingRequestedOrDelivered")
                .apply { isAccessible = true }
                .set(pingController, false)

        Mockito.verify(protocolController, Mockito.never()).restartConnection()
    }
}