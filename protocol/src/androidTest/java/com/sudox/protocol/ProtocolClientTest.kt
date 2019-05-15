package com.sudox.protocol

import android.support.test.runner.AndroidJUnit4
import com.sudox.protocol.models.enums.ConnectionState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProtocolClientTest : Assert() {

    @Test
    fun testSubmitStateChangeEvent() {
        val protocolClient = ProtocolClient("127.0.0.1", 7899)

        protocolClient.submitStateChangeEvent(ConnectionState.CONNECTION_CLOSED)
        assertNotNull(protocolClient.connectionStateChannel.valueOrNull)
        assertEquals(ConnectionState.CONNECTION_CLOSED, protocolClient.connectionStateChannel.value)
    }

    @Test
    fun testConnect() {
        val protocolClient = ProtocolClient("127.0.0.1", 7899)

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
    fun testClose() {
        val protocolClient = ProtocolClient("127.0.0.1", 7899)

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