package com.sudox.protocol.client.serialization

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.nio.ByteBuffer
import kotlin.random.Random

private const val firstParamKey = "boolean"
private const val secondParamKey = "number"
private const val thirdParamKey = "object"
private val firstParamKeyBytes = firstParamKey.toByteArray()
private val secondParamKeyBytes = secondParamKey.toByteArray()
private val thirdParamKeyBytes = thirdParamKey.toByteArray()

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

    @Test
    fun testLong_max() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(20u, 8u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray())
        val result = deserializer.deserialize(buffer)
        assertEquals(Long.MAX_VALUE, result)
    }

    @Test
    fun testLong_min() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(20u, 8u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 128u).toByteArray())
        val result = deserializer.deserialize(buffer)
        assertEquals(Long.MIN_VALUE, result)
    }

    @Test
    fun testString_1_byte_length() {
        val string = "Hello World!"
        val buffer = ByteBuffer.wrap(ubyteArrayOf(30u, 12u, 0u).toByteArray() + string.toByteArray())
        val result = deserializer.deserialize(buffer)
        assertEquals(string, result)
    }

    @Test
    fun testString_2_byte_length() {
        val string = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sed varius enim," +
                " sed consequat urna. Fusce hendrerit, leo ut posuere tincidunt, neque ex tempor diam, id blandit" +
                " est lacus in nisi. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubil" +
                "ia Curae; Suspendisse ut dapibus tellus. Donec ultrices rutrum tincidunt. Etiam nec metus non quam" +
                " rutrum mattis sit amet a metus. Integer sed lorem at arcu gravida semper. Etiam et orci viverra, " +
                "egestas nunc non posuere."

        val buffer = ByteBuffer.wrap(ubyteArrayOf(30u, 244u, 1u).toByteArray() + string.toByteArray())
        val result = deserializer.deserialize(buffer)
        assertEquals(string, result)
    }

    @Test
    fun testBuffer_1_byte_length() {
        val bytes = byteArrayOf(1, 2, 3, 4)
        val buffer = ByteBuffer.wrap(ubyteArrayOf(40u, 4u, 0u).toByteArray() + bytes)
        val result = deserializer.deserialize(buffer)
        assertTrue(result is ByteArray)
        assertArrayEquals(bytes, result as ByteArray)
    }

    @Test
    fun testBuffer_2_byte_length() {
        val bytes = Random.nextBytes(500)
        val buffer = ByteBuffer.wrap(ubyteArrayOf(40u, 244u, 1u).toByteArray() + bytes)
        val result = deserializer.deserialize(buffer)
        assertTrue(result is ByteArray)
        assertArrayEquals(bytes, result as ByteArray)
    }

    @Test
    fun testBoolean_true() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(10u, 1u).toByteArray())
        val result = deserializer.deserialize(buffer)
        assertTrue(result is Boolean)
        assertTrue(result as Boolean)
    }

    @Test
    fun testBoolean_false() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(10u, 0u).toByteArray())
        val result = deserializer.deserialize(buffer)
        assertTrue(result is Boolean)
        assertFalse(result as Boolean)
    }

    @Test
    fun testArrayOfObjects() {
        val `object` = TestSerializable1().apply {
            number = Long.MAX_VALUE
            boolean = true
        }

        val valid = arrayOf(`object`, `object`)
        val buffer = ByteBuffer.wrap(ubyteArrayOf(50u, valid.size.toUByte()).toByteArray() + ubyteArrayOf(60u, 2u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(10u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(20u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray() +
                ubyteArrayOf(60u, 2u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(10u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(20u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray())
        val result = deserializer.deserialize(buffer, `object`::class)

        assertTrue(result is Array<*>)
        assertArrayEquals(valid, result as Array<*>)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testArrayOfLongs() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(50u, 3u, 20u, 1u, 1u, 20u, 8u, 255u, 255u, 255u, 255u, 255u, 255u,
                255u, 127u, 20u, 8u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 128u).toByteArray())

        val result = deserializer.deserialize(buffer)
        assertTrue(result is Array<*>)
        assertArrayEquals(arrayOf(1L, Long.MAX_VALUE, Long.MIN_VALUE), result as Array<Any>)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testArrayOfBooleans() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(50u, 2u, 10u, 0u, 10u, 1u).toByteArray())
        val result = deserializer.deserialize(buffer)
        assertTrue(result is Array<*>)
        assertArrayEquals(arrayOf(false, true), result as Array<Any>)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testArrayOfBooleansWithObjectClass() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(50u, 2u, 10u, 0u, 10u, 1u).toByteArray())
        val result = deserializer.deserialize(buffer, TestSerializable2::class)
        assertTrue(result is Array<*>)
        assertArrayEquals(arrayOf(false, true), result as Array<Any>)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testArrayInArray() {
        val firstArray = arrayOf(1L, Long.MAX_VALUE, Long.MIN_VALUE)
        val secondArray = arrayOf(false, true)
        val array = arrayOf(firstArray, secondArray)

        val buffer = ByteBuffer.wrap(ubyteArrayOf(50u, array.size.toUByte()).toByteArray() + ubyteArrayOf(50u,
                firstArray.size.toUByte(), 20u, 1u, 1u, 20u, 8u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u, 20u,
                8u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 128u).toByteArray() + ubyteArrayOf(50u, secondArray.size.toUByte(),
                10u, 0u, 10u, 1u).toByteArray())

        val result = deserializer.deserialize(buffer)
        assertTrue(result is Array<*>)

        val resultArray = result as Array<out Any>
        assertEquals(array.size, resultArray.size)
        assertArrayEquals(firstArray, resultArray[0] as Array<out Any>)
        assertArrayEquals(secondArray, resultArray[1] as Array<out Any>)
    }

    @Test
    fun testObject() {
        val valid = TestSerializable1().apply {
            number = Long.MAX_VALUE
            boolean = true
        }

        val buffer = ByteBuffer.wrap(ubyteArrayOf(60u, 2u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(10u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(20u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray())

        val result = deserializer.deserialize(buffer, TestSerializable1::class)
        assertTrue(result is TestSerializable1)

        val `object` = result as TestSerializable1
        assertEquals(valid, `object`)
    }

    @Test
    fun testNotEnoughBytesForTypes() {
        val buffer = ByteBuffer.allocate(1).apply { position(1) }
        val result = deserializer.deserialize(buffer)
        assertEquals(null, result)
    }

    @Test
    fun testObjectInObject() {
        val valid = TestSerializable2().apply {
            `object` = TestSerializable3().apply {
                number = Long.MAX_VALUE
                boolean = true
            }

            number = Long.MAX_VALUE
            boolean = true
        }

        val buffer = ByteBuffer.wrap(ubyteArrayOf(60u, 3u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(10u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(20u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray() +
                thirdParamKeyBytes.size.toByte() + thirdParamKeyBytes + ubyteArrayOf(60u, 2u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(10u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(20u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray())

        val result = deserializer.deserialize(buffer, TestSerializable2::class)
        assertTrue(result is TestSerializable2)

        val `object` = result as TestSerializable2
        assertTrue(result.`object` is TestSerializable3)
        assertEquals(valid, `object`)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testObjectInObject_tree_mode() {
        val valid = TestSerializable2().apply {
            `object` = TestSerializable3().apply {
                number = Long.MAX_VALUE
                boolean = true
            }

            number = Long.MAX_VALUE
            boolean = true
        }

        val buffer = ByteBuffer.wrap(ubyteArrayOf(60u, 3u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(10u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(20u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray() +
                thirdParamKeyBytes.size.toByte() + thirdParamKeyBytes + ubyteArrayOf(60u, 2u).toByteArray() +
                firstParamKeyBytes.size.toByte() + firstParamKeyBytes + ubyteArrayOf(10u, 1u).toByteArray() +
                secondParamKeyBytes.size.toByte() + secondParamKeyBytes + ubyteArrayOf(20u, 8u).toByteArray() +
                ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray())

        val result = deserializer.deserialize(buffer)
        assertTrue(result is LinkedHashMap<*, *>)
        val objectTree = result as LinkedHashMap<String, Any>
        val includedObject = result[thirdParamKey]

        assertEquals(true, objectTree[firstParamKey])
        assertEquals(Long.MAX_VALUE, objectTree[secondParamKey])
        assertEquals(includedObject, objectTree[thirdParamKey])

        assertTrue(includedObject is LinkedHashMap<*, *>)
        val includedObjectTree = includedObject as LinkedHashMap<String, Any>

        assertEquals(true, includedObjectTree[firstParamKey])
        assertEquals(Long.MAX_VALUE, includedObjectTree[secondParamKey])
    }

    @Test
    fun testInvalidType() {
        val buffer = ByteBuffer.wrap(byteArrayOf(127, 0, 0))
        val result = deserializer.deserialize(buffer)
        assertEquals(null, result)
    }

    @Test
    fun testBooleanNotEnoughBytes() {
        val buffer = ByteBuffer.wrap(ubyteArrayOf(10u).toByteArray())
        val result = deserializer.deserialize(buffer)
        assertEquals(false, result)
    }

    @Test
    fun testNumberNotEnoughBytes() {
        val buffer0 = ByteBuffer.wrap(ubyteArrayOf(20u).toByteArray())
        val result0 = deserializer.deserialize(buffer0)
        assertEquals(0L, result0)

        val buffer1 = ByteBuffer.wrap(ubyteArrayOf(20u, 5u).toByteArray())
        val result1 = deserializer.deserialize(buffer1)
        assertEquals(0L, result1)
    }

    @Test
    fun testStringNotEnoughBytes() {
        val buffer0 = ByteBuffer.wrap(ubyteArrayOf(30u).toByteArray())
        val result0 = deserializer.deserialize(buffer0)
        assertEquals(null, result0)

        val buffer1 = ByteBuffer.wrap(ubyteArrayOf(30u, 0u, 5u).toByteArray())
        val result1 = deserializer.deserialize(buffer1)
        assertEquals(null, result1)
    }

    @Test
    fun testBufferNotEnoughBytes() {
        val buffer0 = ByteBuffer.wrap(ubyteArrayOf(40u).toByteArray())
        val result0 = deserializer.deserialize(buffer0)
        assertEquals(null, result0)

        val buffer1 = ByteBuffer.wrap(ubyteArrayOf(40u, 0u, 5u).toByteArray())
        val result1 = deserializer.deserialize(buffer1)
        assertEquals(null, result1)
    }

    @Test
    fun testArrayNotEnoughBytes() {
        val buffer0 = ByteBuffer.wrap(ubyteArrayOf(50u).toByteArray())
        val result0 = deserializer.deserialize(buffer0)
        assertEquals(null, result0)
    }

    @Test
    fun testArrayInvalidElement() {
        val buffer0 = ByteBuffer.wrap(ubyteArrayOf(50u, 5u, 10u, 1u).toByteArray())
        val result0 = deserializer.deserialize(buffer0)
        assertEquals(null, result0)
    }

    @Test
    fun testObjectNotEnoughBytes() {
        val buffer0 = ByteBuffer.wrap(ubyteArrayOf(60u).toByteArray())
        val result0 = deserializer.deserialize(buffer0)
        assertEquals(null, result0)
    }

    @Test
    fun testObjectInvalidParam() {
        val buffer0 = ByteBuffer.wrap(ubyteArrayOf(60u, 5u, 1u, 1u).toByteArray())
        val result0 = deserializer.deserialize(buffer0)
        assertEquals(null, result0)
    }

    class TestSerializable1 : Serializable() {

        var boolean: Boolean = false
        var number: Long = 0

        override fun serialize(serializer: Serializer) {}
        override fun deserialize(params: LinkedHashMap<String, Any>) {
            boolean = params[firstParamKey] as Boolean
            number = params[secondParamKey] as Long
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TestSerializable1

            if (boolean != other.boolean) return false
            if (number != other.number) return false

            return true
        }

        override fun hashCode(): Int {
            var result = boolean.hashCode()
            result = 31 * result + number.hashCode()
            return result
        }
    }

    class TestSerializable2 : Serializable() {

        var `object`: TestSerializable3? = null
        var boolean: Boolean = false
        var number: Long = 0

        override fun serialize(serializer: Serializer) {}
        override fun deserialize(params: LinkedHashMap<String, Any>) {
            boolean = params[firstParamKey] as Boolean
            number = params[secondParamKey] as Long
            `object` = readObjectParam(thirdParamKey, TestSerializable3::class, params)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TestSerializable2

            if (`object` != other.`object`) return false
            if (boolean != other.boolean) return false
            if (number != other.number) return false

            return true
        }

        override fun hashCode(): Int {
            var result = `object`?.hashCode() ?: 0
            result = 31 * result + boolean.hashCode()
            result = 31 * result + number.hashCode()
            return result
        }
    }

    class TestSerializable3 : Serializable() {

        var boolean: Boolean = false
        var number: Long = 0

        override fun serialize(serializer: Serializer) {}
        override fun deserialize(params: LinkedHashMap<String, Any>) {
            boolean = params[firstParamKey] as Boolean
            number = params[secondParamKey] as Long
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TestSerializable3

            if (boolean != other.boolean) return false
            if (number != other.number) return false

            return true
        }

        override fun hashCode(): Int {
            var result = boolean.hashCode()
            result = 31 * result + number.hashCode()
            return result
        }
    }
}