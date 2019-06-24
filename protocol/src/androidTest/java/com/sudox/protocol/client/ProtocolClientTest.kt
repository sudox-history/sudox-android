package com.sudox.protocol.client

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class ProtocolClientTest : Assert() {

    private lateinit var callback: ProtocolCallback
    private lateinit var client: ProtocolClient

    @Before
    fun setUp() {
        callback = Mockito.mock(ProtocolCallback::class.java)
        client = ProtocolClient("46.173.214.66", 5000, callback)
    }

    @After
    fun tearDown() {
        client.close()
    }

    @Test
    fun testSendingManyPackets() {
        client.connect()
        Thread.sleep(1000)

        for (i in 1 .. 1000) {
            client.sendMessage("Hello World".toByteArray())
        }

        Thread.sleep(50000)
    }
}