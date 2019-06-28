package com.sudox.protocol.client.serialization

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@ExperimentalUnsignedTypes
class SerializerTest : Assert() {

    private lateinit var serializer: Serializer

    @Before
    fun setUp() {
        serializer = Serializer()
    }

    @Test
    fun testLong_1_byte() {
        val buffer = serializer.serialize(1L, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(2u, 1u, 1u).toByteArray()
        assertArrayEquals(valid, result)
    }

    @Test
    fun testLong_1_byte_negative() {
        val buffer = serializer.serialize(-1L, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(2u, 1u, 255u).toByteArray()
        assertArrayEquals(valid, result)
    }

    @Test
    fun testLong_2_byte() {
        val buffer = serializer.serialize(500L, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(2u, 2u, 244u, 1u).toByteArray()
        assertArrayEquals(valid, result)
    }

    @Test
    fun testLong_2_byte_negative() {
        val buffer = serializer.serialize(-500L, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(2u, 2u, 12u, 254u).toByteArray()
        assertArrayEquals(valid, result)
    }

    @Test
    fun testLong_max() {
        val buffer = serializer.serialize(Long.MAX_VALUE, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(2u, 8u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray()
        assertArrayEquals(valid, result)
    }

    @Test
    fun testLong_negative() {
        val buffer = serializer.serialize(Long.MIN_VALUE, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(2u, 8u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 128u).toByteArray()
        assertArrayEquals(valid, result)
    }

    @Test
    fun testString_string_1_byte_length() {
        val string = "Hello World!"
        val bytes = string.toByteArray()
        val buffer = serializer.serialize(string, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(3u, 12u, 0u).toByteArray() + bytes
        assertArrayEquals(valid, result)
    }

    @Test
    fun testString_string_2_byte_length() {
        val string = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sed varius enim," +
                " sed consequat urna. Fusce hendrerit, leo ut posuere tincidunt, neque ex tempor diam, id blandit" +
                " est lacus in nisi. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubil" +
                "ia Curae; Suspendisse ut dapibus tellus. Donec ultrices rutrum tincidunt. Etiam nec metus non quam" +
                " rutrum mattis sit amet a metus. Integer sed lorem at arcu gravida semper. Etiam et orci viverra, " +
                "egestas nunc non posuere."

        val buffer = serializer.serialize(string, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(3u, 244u, 1u).toByteArray() + string.toByteArray()
        assertArrayEquals(valid, result)
    }

    @Test
    fun testBuffer_1_byte_length() {
        val bytes = byteArrayOf(1, 2, 3, 4)
        val buffer = serializer.serialize(bytes, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(4u, 4u, 0u).toByteArray() + bytes
        assertArrayEquals(valid, result)
    }

    @Test
    fun testBuffer_2_byte_length() {
        val bytes = Random.nextBytes(500)
        val buffer = serializer.serialize(bytes, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(4u, 244u, 1u).toByteArray() + bytes
        assertArrayEquals(valid, result)
    }

    @Test
    fun testBoolean_true() {
        val buffer = serializer.serialize(true, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(1u, 1u).toByteArray()
        assertArrayEquals(valid, result)
    }

    @Test
    fun testBoolean_false() {
        val buffer = serializer.serialize(false, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(1u, 0u).toByteArray()
        assertArrayEquals(valid, result)
    }

    @Test
    fun testArrayOfObjects() {
        val firstParamKey = "boolean"
        val secondParamKey = "number"
        val firstParamKeyBytes = firstParamKey.toByteArray()
        val secondParamKeyBytes = secondParamKey.toByteArray()
        val `object` = object : Serializable() {
            override fun deserialize(params: LinkedHashMap<String, Any>) {}
            override fun serialize(serializer: Serializer) {
                serializer.writeParam(this, firstParamKey, true)
                serializer.writeParam(this, secondParamKey, Long.MAX_VALUE)
            }
        }

        val array = arrayOf(`object`, `object`)
        val buffer = serializer.serialize(array, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(5u, array.size.toUByte()).toByteArray() + ubyteArrayOf(6u, 2u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(1u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(2u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray() +
                ubyteArrayOf(6u, 2u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(1u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(2u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray()

        assertArrayEquals(valid, result)
    }

    @Test
    fun testArrayOfLongs() {
        val array = longArrayOf(1L, Long.MAX_VALUE, Long.MIN_VALUE)
        val buffer = serializer.serialize(array, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(5u, 3u, 2u, 1u, 1u, 2u, 8u, 255u,
                255u, 255u, 255u, 255u, 255u, 255u, 127u, 2u,
                8u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 128u)

        assertArrayEquals(valid.toByteArray(), result)
    }

    @Test
    fun testArrayOfBooleans() {
        val array = booleanArrayOf(false, true)
        val buffer = serializer.serialize(array, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(5u, array.size.toUByte()).toByteArray() + ubyteArrayOf(1u, 0u).toByteArray() +
                ubyteArrayOf(1u, 1u).toByteArray()

        assertArrayEquals(valid, result)
    }

    @Test
    fun testArrayInArray() {
        val firstArray = longArrayOf(1L, Long.MAX_VALUE, Long.MIN_VALUE)
        val secondArray = booleanArrayOf(false, true)
        val array = arrayOf(firstArray, secondArray)
        val buffer = serializer.serialize(array, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(5u, array.size.toUByte()).toByteArray() +
                ubyteArrayOf(5u, firstArray.size.toUByte(), 2u, 1u, 1u, 2u, 8u, 255u, 255u, 255u, 255u, 255u, 255u,
                        255u, 127u, 2u, 8u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 128u).toByteArray() +
                ubyteArrayOf(5u, secondArray.size.toUByte(), 1u, 0u, 1u, 1u).toByteArray()

        assertArrayEquals(valid, result)
    }

    @Test
    fun testObject() {
        val firstParamKey = "boolean"
        val secondParamKey = "number"
        val firstParamKeyBytes = firstParamKey.toByteArray()
        val secondParamKeyBytes = secondParamKey.toByteArray()
        val `object` = object : Serializable() {
            override fun deserialize(params: LinkedHashMap<String, Any>) {}
            override fun serialize(serializer: Serializer) {
                serializer.writeParam(this, firstParamKey, true)
                serializer.writeParam(this, secondParamKey, Long.MAX_VALUE)
            }
        }

        val buffer = serializer.serialize(`object`, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(6u, 2u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(1u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(2u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray()

        assertArrayEquals(valid, result)
    }

    @Test
    fun testObject_included() {
        val firstParamKey = "boolean"
        val secondParamKey = "number"
        val thirdParamKey = "object"
        val firstParamKeyBytes = firstParamKey.toByteArray()
        val secondParamKeyBytes = secondParamKey.toByteArray()
        val thirdParamKeyBytes = thirdParamKey.toByteArray()

        val secondObject = object : Serializable() {
            override fun deserialize(params: LinkedHashMap<String, Any>) {}
            override fun serialize(serializer: Serializer) {
                serializer.writeParam(this, firstParamKey, true)
                serializer.writeParam(this, secondParamKey, Long.MAX_VALUE)
            }
        }

        val firstObject = object : Serializable() {
            override fun deserialize(params: LinkedHashMap<String, Any>) {}
            override fun serialize(serializer: Serializer) {
                serializer.writeParam(this, firstParamKey, true)
                serializer.writeParam(this, secondParamKey, Long.MAX_VALUE)
                serializer.writeParam(this, thirdParamKey, secondObject)
            }
        }

        val buffer = serializer.serialize(firstObject, 0)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(6u, 3u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(1u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(2u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray() +
                thirdParamKeyBytes.size.toByte() + thirdParamKeyBytes + ubyteArrayOf(6u, 2u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(1u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(2u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray()

        assertArrayEquals(valid, result)
    }

    @Test
    fun testSerializeOffset() {
        val buffer = serializer.serialize(true, 2)
        val result = ByteArray(buffer.limit()).apply { buffer.get(this) }
        val valid = ubyteArrayOf(0u, 0u, 1u, 1u).toByteArray()
        assertArrayEquals(valid, result)
    }
}