package com.sudox.protocol

import android.support.test.runner.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class ProtocolClientTest : Assert() {

    @Test
    fun testConnect() {
        val callback = Mockito.mock(ProtocolCallback::class.java)
        val protocolClient = ProtocolClient("127.0.0.1", 7899, callback)

        protocolClient.connect()

        val protocolController = protocolClient.protocolController
        assertNotNull(protocolController)
        assertTrue(protocolController!!.isAlive)

        // Testing ...
        protocolClient.connect()
        assertEquals(protocolController, protocolClient.protocolController)
        assertTrue(protocolController.isAlive)
    }

    @Test
    fun testSendMessage_inactive_controller() {
        val callback = Mockito.mock(ProtocolCallback::class.java)
        val protocolClient = ProtocolClient("127.0.0.1", 7899, callback)

        assertFalse(protocolClient.sendMessage("test".toByteArray()))
    }

    @Test
    fun testClose() {
        val callback = Mockito.mock(ProtocolCallback::class.java)
        val protocolClient = ProtocolClient("127.0.0.1", 7899, callback)

        // Testing ...
        protocolClient.close()
        assertNull(protocolClient.protocolController)

        protocolClient.connect()
        val protocolController = protocolClient.protocolController

        protocolClient.close()
        assertTrue(protocolController!!.isInterrupted)
        assertNull(protocolClient.protocolController)
    }
}