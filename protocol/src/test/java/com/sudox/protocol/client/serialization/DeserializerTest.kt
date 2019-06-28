package com.sudox.protocol.client.serialization

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.nio.ByteBuffer

@ExperimentalUnsignedTypes
class DeserializerTest : Assert() {

    private lateinit var deserializer: Deserializer

    @Before
    fun setUp() {
        deserializer = Deserializer()
    }

    @Test
    fun testLong_1_byte() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(20u, 1u, 1u).toByteArray())
        val result = deserializer.deserialize(buffer)
        assertEquals(1L, result)
    }

    @Test
    fun testLong_1_byte_negative() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(20u, 1u, 255u).toByteArray())
        val result = deserializer.deserialize(buffer)
        assertEquals(-1L, result)
    }

    @Test
    fun testLong_2_byte() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(20u, 2u, 244u, 1u).toByteArray())
        val result = deserializer.deserialize(buffer)
        assertEquals(500L, result)
    }

    @Test
    fun testLong_2_byte_negative() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(20u, 2u, 12u, 254u).toByteArray())
        val result = deserializer.deserialize(buffer)
        assertEquals(-500L, result)
    }

//    @Test
//    fun testString() {
//        val string = "Hello World!"
//        val bytes = string.toByteArray()
//        val buffer = deserializer.deserialize(buffer)
//        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
//        val valid = ubyteArrayOf(30u, 12u, 0u).toByteArray() + bytes
//        assertArrayEquals(valid, result)
//    }
}