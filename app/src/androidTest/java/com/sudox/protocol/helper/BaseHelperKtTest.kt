package com.sudox.protocol.helper

import org.hamcrest.core.Is
import org.junit.Assert.*
import org.junit.Test

class BaseHelperKtTest {

    @Test
    fun testRandomBase64String() {
        val testVerifying = randomBase64String(32)

        // Verifying
        assertEquals(testVerifying.length, 44)
    }

    @Test
    fun testDecodeBase64() {
        val testInput = "0K/RgNC40Log0LPQsNC90LTQvtC9"
                .toByteArray()

        val validResult = "Ярик гандон"
                .toByteArray()

        // Decode from base
        val result = decodeBase64(testInput)

        // Validate
        assertThat(result, Is.`is`(validResult))
    }

    @Test
    fun testDecodeBase64_bad() {
        val testInput = "§"
                .toByteArray()

        // Decode from base
        val result = decodeBase64(testInput)

        // Validate
        assertNull(result)
    }

    @Test
    fun testDecodeBase64String() {
        val testInput = "0K/RgNC40Log0LPQsNC90LTQvtC9"
        val validResult = "Ярик гандон"
                .toByteArray()

        // Decode from base
        val result = decodeBase64String(testInput)

        // Validate
        assertThat(result, Is.`is`(validResult))
    }

    @Test
    fun testDecodeBase64String_bad() {
        val testInput = "§"

        // Decode from base
        val result = decodeBase64String(testInput)

        // Validate
        assertNull(result)
    }

    @Test
    fun testEncodeBase64() {
        val validResult = "0K/RgNC40Log0LPQsNC90LTQvtC9"
                .toByteArray()

        val testInput = "Ярик гандон"
                .toByteArray()

        // Decode from base
        val result = encodeBase64(testInput)

        // Validate
        assertThat(result, Is.`is`(validResult))
    }

    @Test
    fun testEncodeBase64String() {
        val validResult = "0K/RgNC40Log0LPQsNC90LTQvtC9"
                .toByteArray()

        val testInput = "Ярик гандон"

        // Decode from base
        val result = encodeBase64String(testInput)

        // Validate
        assertThat(result, Is.`is`(validResult))
    }
}