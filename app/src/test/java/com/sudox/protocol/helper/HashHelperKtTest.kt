package com.sudox.protocol.helper

import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import org.junit.Test

import org.junit.Assert.*

class HashHelperKtTest {

    @Test
    fun testGetHash() {
        val testInput = "Hello World!"
                .toByteArray()

        val validResult = "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"
                .toByteArray()

        // Get the hash
        val result = getHash(testInput)

        // Print results
        println("Valid: ${String(validResult)}")
        println("Current: ${String(result)}")

        // Assert
        assertEquals(result.size, validResult.size)
        assertThat(result, Is.`is`(IsEqual.equalTo(validResult)))
    }

    @Test
    fun testHashString() {
        val testInput = "Hello World!"
        val validResult = "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"

        // Get the hash
        val result = getHashString(testInput)

        // Print results
        println("Valid: $validResult")
        println("Current: $result")

        // Assert
        assertEquals(validResult, result)
    }
}