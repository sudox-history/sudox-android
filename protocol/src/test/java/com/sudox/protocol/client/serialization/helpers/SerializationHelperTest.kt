package com.sudox.protocol.client.serialization.helpers

import org.junit.Assert
import org.junit.Test
import java.nio.ByteBuffer

@ExperimentalUnsignedTypes
class SerializationHelperTest : Assert() {

    @Test
    fun testWriteIntLE() {
        var buffer = ByteBuffer.allocateDirect(128)
        var valid = ubyteArrayOf(1u).toByteArray()
        buffer.writeIntLE(1, 1)
        buffer.flip()
        var bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(255u).toByteArray()
        buffer.writeIntLE(-1, 1)
        buffer.flip()
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(244u, 1u).toByteArray()
        buffer.writeIntLE(500, 2)
        buffer.flip()
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(12u, 254u).toByteArray()
        buffer.writeIntLE(-500, 2)
        buffer.flip()
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(255u, 255u, 255u, 127u).toByteArray()
        buffer.writeIntLE(Int.MAX_VALUE, 4)
        buffer.flip()
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 128u).toByteArray()
        buffer.writeIntLE(Int.MIN_VALUE, 4)
        buffer.flip()
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)
    }

    @Test
    fun testWriteIntLE_position() {
        var buffer = ByteBuffer.allocateDirect(128)
        var valid = ubyteArrayOf(0u, 0u, 0u, 1u).toByteArray()
        buffer.writeIntLE(1, 1, 3)
        buffer.limit(4)
        var bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 255u).toByteArray()
        buffer.writeIntLE(-1, 1, 3)
        buffer.limit(4)
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 244u, 1u).toByteArray()
        buffer.writeIntLE(500, 2, 3)
        buffer.limit(5)
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 12u, 254u).toByteArray()
        buffer.writeIntLE(-500, 2, 3)
        buffer.limit(5)
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 255u, 255u, 255u, 127u).toByteArray()
        buffer.writeIntLE(Int.MAX_VALUE, 4, 3)
        buffer.limit(7)
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 0u, 0u, 0u, 128u).toByteArray()
        buffer.writeIntLE(Int.MIN_VALUE, 4, 3)
        buffer.limit(7)
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)
    }

    @Test
    fun testWriteLongLE() {
        var buffer = ByteBuffer.allocateDirect(128)
        var valid = ubyteArrayOf(1u).toByteArray()
        buffer.writeLongLE(1, 1)
        buffer.flip()
        var bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(255u).toByteArray()
        buffer.writeLongLE(-1, 1)
        buffer.flip()
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(244u, 1u).toByteArray()
        buffer.writeLongLE(500, 2)
        buffer.flip()
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(12u, 254u).toByteArray()
        buffer.writeLongLE(-500, 2)
        buffer.flip()
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray()
        buffer.writeLongLE(Long.MAX_VALUE, 8)
        buffer.flip()
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 0u, 0u, 0u, 0u, 128u).toByteArray()
        buffer.writeLongLE(Long.MIN_VALUE, 8)
        buffer.flip()
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)
    }

    @Test
    fun testWriteLongLE_position() {
        var buffer = ByteBuffer.allocateDirect(128)
        var valid = ubyteArrayOf(0u, 0u, 0u, 1u).toByteArray()
        buffer.writeLongLE(1, 1, 3)
        buffer.limit(4)
        var bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 255u).toByteArray()
        buffer.writeLongLE(-1, 1, 3)
        buffer.limit(4)
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 244u, 1u).toByteArray()
        buffer.writeLongLE(500, 2, 3)
        buffer.limit(5)
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 12u, 254u).toByteArray()
        buffer.writeLongLE(-500, 2, 3)
        buffer.limit(5)
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray()
        buffer.writeLongLE(Long.MAX_VALUE, 8, 3)
        buffer.limit(11)
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)

        buffer = ByteBuffer.allocateDirect(128)
        valid = ubyteArrayOf(0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 128u).toByteArray()
        buffer.writeLongLE(Long.MIN_VALUE, 8, 3)
        buffer.limit(11)
        bytes = ByteArray(buffer.limit()).apply { buffer.get(this) }
        assertArrayEquals(valid, bytes)
    }

    @Test
    fun testReadUIntLE() {
        var buffer = ByteBuffer.wrap(ubyteArrayOf(1u).toByteArray())
        assertEquals(1, buffer.readUIntLE(1))

        buffer = ByteBuffer.wrap(ubyteArrayOf(255u).toByteArray())
        assertEquals(255, buffer.readUIntLE(1))

        buffer = ByteBuffer.wrap(ubyteArrayOf(244u, 1u).toByteArray())
        assertEquals(500, buffer.readUIntLE(2))

        buffer = ByteBuffer.wrap(ubyteArrayOf(12u, 254u).toByteArray())
        assertEquals(65036, buffer.readUIntLE(2))

        buffer = ByteBuffer.wrap(ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u).toByteArray())
        assertEquals(UInt.MAX_VALUE, buffer.readUIntLE(8).toUInt())

        buffer = ByteBuffer.wrap(ubyteArrayOf(0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u).toByteArray())
        assertEquals(UInt.MIN_VALUE, buffer.readUIntLE(8).toUInt())
    }

    @Test
    fun testReadLongLE() {
        var buffer = ByteBuffer.wrap(ubyteArrayOf(1u).toByteArray())
        assertEquals(1L, buffer.readLongLE(1))

        buffer = ByteBuffer.wrap(ubyteArrayOf(255u).toByteArray())
        assertEquals(-1L, buffer.readLongLE(1))

        buffer = ByteBuffer.wrap(ubyteArrayOf(244u, 1u).toByteArray())
        assertEquals(500L, buffer.readLongLE(2))

        buffer = ByteBuffer.wrap(ubyteArrayOf(12u, 254u).toByteArray())
        assertEquals(-500L, buffer.readLongLE(2))

        buffer = ByteBuffer.wrap(ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u).toByteArray())
        assertEquals(Long.MAX_VALUE, buffer.readLongLE(8))

        buffer = ByteBuffer.wrap(ubyteArrayOf(0u, 0u, 0u, 0u, 0u, 0u, 0u, 128u).toByteArray())
        assertEquals(Long.MIN_VALUE, buffer.readLongLE(8))
    }

    @Test
    fun testCalculateOctets() {
        assertEquals(1, 0L.calculateOctets())

        assertEquals(1, 127L.calculateOctets())
        assertEquals(2, 128L.calculateOctets())
        assertEquals(2, 255L.calculateOctets())
        assertEquals(2, 32767L.calculateOctets())
        assertEquals(3, 32768L.calculateOctets())
        assertEquals(3, 65535L.calculateOctets())
        assertEquals(4, 2147483647L.calculateOctets())
        assertEquals(5, 2147483648L.calculateOctets())
        assertEquals(5, 549755813887L.calculateOctets())
        assertEquals(6, 549755813888L.calculateOctets())
        assertEquals(8, Long.MAX_VALUE.calculateOctets())

        assertEquals(1, (-128L).calculateOctets())
        assertEquals(2, (-129L).calculateOctets())
        assertEquals(2, (-255L).calculateOctets())
        assertEquals(2, (-32767L).calculateOctets())
        assertEquals(2, (-32768L).calculateOctets())
        assertEquals(3, (-65535L).calculateOctets())
        assertEquals(4, (-2147483648L).calculateOctets())
        assertEquals(5, (-549755813887L).calculateOctets())
        assertEquals(6, (-549755813888L).calculateOctets())
        assertEquals(8, Long.MIN_VALUE.calculateOctets())
    }
}