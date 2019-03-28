package com.sudox.protocol.helpers

import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.IOException

class SocketHelperTest : Assert() {

    @Test
    fun testReadData_stream_ended() {
        val byteArrayInputStream = BufferedInputStream(ByteArrayInputStream(ByteArray(0)))
        val buffer = ByteArray(1024)

        // Testing ...
        byteArrayInputStream.close()

        // Verifying ...
        assertFalse(byteArrayInputStream.readData(buffer))
    }

    @Test
    fun testReadData_read_data() {
        val written = ByteArray(1024)
        val byteArrayInputStream =  BufferedInputStream(ByteArrayInputStream(written))
        val buffer = ByteArray(1024)

        // Verifying ...
        assertTrue(byteArrayInputStream.readData(buffer))
        assertArrayEquals(written, buffer)
    }

    @Test
    fun testReadData_io_exception() {
        val inputStream = Mockito.mock(BufferedInputStream::class.java)
        val buffer = ByteArray(1024)

        // Preparing ...
        Mockito.`when`(inputStream.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(IOException())

        // Verifying ...
        assertFalse(inputStream.readData(buffer))
    }
}