package com.sudox.protocol.helper

import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class HexHelperKtTest {

    @Test
    fun testRandomHexString() {
        val testLength = Random().nextInt(512)

        // Generate the string
        val hexString = randomHexString(testLength)

        // Print code string
        println("Result: $hexString")

        // Check the length
        assertEquals(hexString.length, testLength)

        // Check the format
        hexString.forEach {
            if (!(it in '0'..'9' || it in 'a'..'f')) {
                // Invalid symbol
                fail("String contains non-hex symbol: $it")
            }
        }
    }

    @Test
    fun testDecodeHex() {
        val testInput = "4c6f6e646f6e20697320746865206361706974616c206f66204772656174204272697461696e"
        val validResult = "London is the capital of Great Britain"
                .toByteArray()

        // Decode the hex
        val result = decodeHex(testInput)

        // Print results
        println("Valid: ${String(validResult)}")
        println("Current: ${String(result)}")

        // Assert
        assertEquals(result.size, validResult.size)
        assertThat(result, Is.`is`(IsEqual.equalTo(validResult)))
    }

    @Test
    fun testDecodeHexBytes() {
        val testInput = "4c6f6e646f6e20697320746865206361706974616c206f66204772656174204272697461696e"
                .toByteArray()
        val validResult = "London is the capital of Great Britain"
                .toByteArray()

        // Decode the hex
        val result = decodeHexBytes(testInput)

        // Print results
        println("Valid: ${String(validResult)}")
        println("Current: ${String(result)}")

        // Assert
        assertEquals(result.size, validResult.size)
        assertThat(result, Is.`is`(IsEqual.equalTo(validResult)))
    }

    @Test
    fun testEncodeHex() {
        val testInput = "London is the capital of Great Britain"
        val validResult = "4c6f6e646f6e20697320746865206361706974616c206f66204772656174204272697461696e"
                .toByteArray()

        // Encode the hex
        val result = encodeHex(testInput)

        // Print results
        println("Valid: ${String(validResult)}")
        println("Current: ${String(result)}")

        // Assert
        assertEquals(result.size, validResult.size)
        assertThat(result, Is.`is`(IsEqual.equalTo(validResult)))
    }

    @Test
    fun testEncodeHexBytes() {
        val testInput = "London is the capital of Great Britain"
                .toByteArray()

        val validResult = "4c6f6e646f6e20697320746865206361706974616c206f66204772656174204272697461696e"
                .toByteArray()

        // Encode the hex
        val result = encodeHexBytes(testInput)

        // Print results
        println("Valid: ${String(validResult)}")
        println("Current: ${String(result)}")

        // Assert
        assertEquals(result.size, validResult.size)
        assertThat(result, Is.`is`(IsEqual.equalTo(validResult)))
    }
}