package com.sudox.protocol.helper

import com.sudox.protocol.model.SymmetricKey
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
        val payloadObject = performDataForEncrypt(symmetricKey, testEvent, testMessage)

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
}