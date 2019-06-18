package com.sudox.protocol.helpers

import org.junit.Assert
import org.junit.Test
import java.nio.ByteBuffer

class SerializationHelperTest : Assert() {

    @Test
    fun testIntBE() {
        val first = 255.toBytesBE(1)
        val second = 1250.toBytesBE(2)
        val third = (-1250).toBytesBE(2)

        assertEquals(1, first.size)
        assertEquals(2, second.size)
        assertEquals(2, third.size)

        assertEquals(255, first.toIntFromBE())
        assertEquals(1250, second.toIntFromBE())
        assertEquals(-1250, third.toIntFromBE())
    }

    @Test
    fun testIntBEBuffer() {
        val buffer = ByteBuffer.allocate(5)

        buffer.apply {
            writeIntBE(255, 1)
            writeIntBE(1250, 2)
            writeIntBE(-1250, 2)
            rewind()
        }

        assertEquals(255, buffer.readIntBE(1))
        assertEquals(1250, buffer.readIntBE(2))
        assertEquals(-1250, buffer.readIntBE(2))
    }

    @Test
    fun testSerialize_single_slice() {
        val slice = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val buffer = serializePacket(arrayOf(slice))

        assertEquals(slice.size + (2 * LENGTH_HEADER_SIZE_IN_BYTES), buffer.readIntBE(LENGTH_HEADER_SIZE_IN_BYTES))
        assertEquals(slice.size, buffer.readIntBE(LENGTH_HEADER_SIZE_IN_BYTES))

        val written = ByteArray(slice.size)
        buffer.get(written)
        assertArrayEquals(slice, written)
    }

    @Test
    fun testSerialize_many_slice() {
        val first = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val second = byteArrayOf(13, 14, 15)
        val buffer = serializePacket(arrayOf(first, second))

        assertEquals(first.size + second.size + (3 * LENGTH_HEADER_SIZE_IN_BYTES),
                buffer.readIntBE(LENGTH_HEADER_SIZE_IN_BYTES))

        assertEquals(first.size, buffer.readIntBE(LENGTH_HEADER_SIZE_IN_BYTES))
        val writtenFirst = ByteArray(first.size)
        buffer.get(writtenFirst)
        assertArrayEquals(first, writtenFirst)

        assertEquals(second.size, buffer.readIntBE(LENGTH_HEADER_SIZE_IN_BYTES))
        val writtenSecond = ByteArray(second.size)
        buffer.get(writtenSecond)
        assertArrayEquals(second, writtenSecond)
    }

    @Test
    fun testWriteSlice() {
        val bytes = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val buffer = ByteBuffer.allocate(bytes.size + LENGTH_HEADER_SIZE_IN_BYTES)

        buffer.apply {
            writePacketSlice(bytes)
            rewind()
        }

        assertEquals(bytes.size, buffer.readIntBE(LENGTH_HEADER_SIZE_IN_BYTES))

        val written = ByteArray(bytes.size)
        buffer.get(written)
        assertArrayEquals(bytes, written)
    }

    @Test
    fun testDeserializePacketSlices() {
        val packet = byteArrayOf(0, 0, 12.toByte(), 0, 0, 1.toByte(), 127, 0, 0, 2.toByte(), 1, 1)
        val dataBytes = packet.copyOfRange(LENGTH_HEADER_SIZE_IN_BYTES, packet.size)
        val buffer = ByteBuffer.wrap(dataBytes)
        val slices = deserializePacketSlices(buffer)

        assertNotNull(slices)
        assertEquals(2, slices!!.size)
        assertArrayEquals(byteArrayOf(127), slices[0])
        assertArrayEquals(byteArrayOf(1, 1), slices[1])
    }

    @Test
    fun testDeserializePacketSlices_negative_length() {
        val length = (-5).toBytesBE(3)
        val packet = byteArrayOf(0, 0, 12.toByte(), length[0], length[1], length[2], 127, 0, 0, 2.toByte(), 1, 1)
        val dataBytes = packet.copyOfRange(LENGTH_HEADER_SIZE_IN_BYTES, packet.size)
        val buffer = ByteBuffer.wrap(dataBytes)
        val slices = deserializePacketSlices(buffer)

        assertNull(slices)
    }

    @Test
    fun testDeserializePacketSlices_not_enough_length() {
        val packet = byteArrayOf(0, 0, 12.toByte(), 0, 0, 5.toByte(), 127)
        val dataBytes = packet.copyOfRange(LENGTH_HEADER_SIZE_IN_BYTES, packet.size)
        val buffer = ByteBuffer.wrap(dataBytes)
        val slices = deserializePacketSlices(buffer)

        assertNull(slices)
    }

    @Test
    fun testReadPacketSlice() {
        val sliceBytesWithHeader = byteArrayOf(0, 0, 2.toByte(), 1, 1)
        val sliceBytesWithoutHeader = byteArrayOf(1, 1)
        val buffer = ByteBuffer.wrap(sliceBytesWithHeader)
        val readBytes = buffer.readPacketSlice()

        assertArrayEquals(sliceBytesWithoutHeader, readBytes)
    }

    @Test
    fun testReadPacketSlice_length_negative() {
        val length = (-5).toBytesBE(3)
        val sliceBytesWithHeader = byteArrayOf(length[0], length[1], length[2], 127)
        val buffer = ByteBuffer.wrap(sliceBytesWithHeader)
        val readBytes = buffer.readPacketSlice()

        assertNull(readBytes)
    }
}