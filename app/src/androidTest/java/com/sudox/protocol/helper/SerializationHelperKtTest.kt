package com.sudox.protocol.helper

import com.sudox.protocol.model.SymmetricKey
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class SerializationHelperKtTest : Assert() {

    @Test
    fun testPerformDataForEncrypt() {
        val symmetricKey = SymmetricKey()

        // Init the symmetric key
        symmetricKey.generate()
        symmetricKey.update()

        val testEvent = "message"
        val testMessage = "Hello!"

        // Verified
        val payloadObject = prepareDataForEncrypt(symmetricKey, testEvent, testMessage)

        // Testing data
        val hash = getHashString(symmetricKey.random + getHashString(testEvent) + getHashString(testMessage))
        val random = payloadObject.payloadObject.get("random")
        val event = payloadObject.payloadObject.get("event")
        val msg = payloadObject.payloadObject.get("msg")

        // Verify
        assertEquals(payloadObject.hash, hash)
        assertEquals(symmetricKey.random, random)
        assertEquals(testEvent, event)
        assertEquals(testMessage, msg)
    }

    @Test
    fun testCheckHashes_success() {
        val testRandom = "261ea9378f03b93186408f2646a54fe291a06e3a5c0c10862332f693062fc108"
        val testEvent = "auth.sendCode"
        val testMessage = "{\"anton\":\"Гандон\"}"
        val validHash = "3455856a6b239a5f11a9048e1215447c6d08e2148728d109d70057bca5177392"

        // Generate payload
        val payloadObject = with(JSONObject()) {
            put("event", testEvent)
            put("random", testRandom)
            put("msg", testMessage)
        }

        // Testing
        val result = checkHashes(validHash, payloadObject.toString())

        // Validate
        assertTrue(result)
    }

    @Test
    fun testCheckHashes_fail() {
        val testRandom = "261ea9378f03b93186408f2646a54fe291a06e3a5c0c10862332f693062fc108"
        val testEvent = "auth.sendCode"
        val testMessage = "{\"anton\":\"Гандон\"}"
        val validHash = "3455856a6b239a5f11a9048e1215447c6d08e2148728d109d70057bca5177392"

        // Generate payload
        val payloadObject = with(JSONObject()) {
            put("event", testEvent + "1")
            put("random", testRandom + "1")
            put("msg", testMessage + "1")
        }

        // Testing
        val result = checkHashes(validHash, payloadObject.toString())

        // Validate
        assertFalse(result)
    }

    @Test
    fun testPerformDataForClient() {
        val testRandom = "261ea9378f03b93186408f2646a54fe291a06e3a5c0c10862332f693062fc108"
        val testEvent = "auth.sendCode"
        val testMessage = "{\"anton\":\"Гандон\"}"

        // Generate payload
        val payloadObject = with(JSONObject()) {
            put("event", testEvent)
            put("random", testRandom)
            put("msg", testMessage)
        }

        // Testing
        val performDataForClient = prepareDataForClient(payloadObject.toString())

        // Validate
        assertEquals(performDataForClient.event, testEvent)
        assertEquals(performDataForClient.message, testMessage)
    }
}