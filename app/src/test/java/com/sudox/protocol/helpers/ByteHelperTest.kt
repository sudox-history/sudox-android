package com.sudox.protocol.helpers

import org.junit.Assert
import org.junit.Test

class ByteHelperTest : Assert() {

    @Test
    fun testRemoveLeadingZeros_empty_array() {
        val input = byteArrayOf()
        val output = byteArrayOf()
        val result = removeLeadingZeros(input)

        assertArrayEquals(output, result)
    }

    @Test
    fun testRemoveLeadingZeros_singleton_array() {
        val input = byteArrayOf(0)
        val output = byteArrayOf(0)
        val result = removeLeadingZeros(input)

        assertArrayEquals(output, result)
    }

    @Test
    fun testRemoveLeadingZeros_long_array() {
        val input = byteArrayOf(0, 0, 0, 0, 1)
        val output = byteArrayOf(1)
        val result = removeLeadingZeros(input)

        assertArrayEquals(output, result)
    }
}