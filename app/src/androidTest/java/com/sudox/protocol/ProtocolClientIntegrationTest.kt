package com.sudox.protocol

import org.junit.Test

class ProtocolClientIntegrationTest {

    @Test
    fun testHandshake() {
        val protocolClient = ProtocolClient()

        // Testing
        val result = protocolClient.connect()

    }
}