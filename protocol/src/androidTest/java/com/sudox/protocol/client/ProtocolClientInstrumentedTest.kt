package com.sudox.protocol.client

import android.util.Log
import com.sudox.protocol.client.serialization.Deserializer
import com.sudox.protocol.client.serialization.Serializer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ProtocolClientInstrumentedTest : Assert() {

    private lateinit var protocolClient: ProtocolClient
    private lateinit var protocolCallback: ProtocolCallbackMock
    private lateinit var serializer: Serializer
    private lateinit var deserializer: Deserializer

    @Before
    fun setUp() {
        protocolCallback = ProtocolCallbackMock()
        protocolClient = ProtocolClient("46.173.214.66", 5000, protocolCallback)
        serializer = Serializer()
        deserializer = Deserializer()
    }

    @After
    fun tearDown() {
        protocolClient.close()
    }

    @Test
    fun testIntegration() {
        Log.d("PINCProto", "Connecting ...")

        protocolClient.connect()
        protocolCallback.startedSemaphore.acquire()

        if (!protocolCallback.connected) {
            fail("Connection not installed!")
        }

        val paramsArray = arrayOf("core.getVersion", "Hello Server!")
        val message = serializer.serialize(paramsArray, 0).array()
        protocolClient.sendMessage(message)

        protocolCallback.messagesSemaphore.acquire()
        val receivedBytes = protocolCallback.lastMessage

        if (receivedBytes == null) {
            fail("Response not received!")
        }

        val received = deserializer.deserialize(receivedBytes!!, null)

        if (received is Array<*>) {
            val method = received[0]
            val params = received[1] as? LinkedHashMap<*, *>

            Log.d("PINCProto", "Received method: $method")
            Log.d("PINCProto", "Received version: ${params?.get("version")}")
        } else {
            fail("Invalid response format!")
        }
    }
}