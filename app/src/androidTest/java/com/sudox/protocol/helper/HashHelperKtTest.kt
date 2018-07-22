package com.sudox.protocol.helper

import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import org.junit.Test

import org.junit.Assert.*

class HashHelperKtTest {

    @Test
    fun testGetHash() {
        val testInput = "Hello world"
                .toByteArray()

        val validResult = "ZOyIygCyaOW6GjVnihtTFtIS9PNmskdyMlNKiuyjfzw="
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
        val testInput = "Hello world"
        val validResult = "ZOyIygCyaOW6GjVnihtTFtIS9PNmskdyMlNKiuyjfzw="

        // Get the hash
        val result = getHashString(testInput)

        // Print results
        println("Valid: $validResult")
        println("Current: $result")

        // Assert
        assertEquals(validResult, result)
    }
}