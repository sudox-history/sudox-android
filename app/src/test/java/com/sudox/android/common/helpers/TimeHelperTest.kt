package com.sudox.android.common.helpers

import android.content.Context
import android.text.format.DateFormat
import com.sudox.android.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(System::class, DateFormat::class, fullyQualifiedNames = ["com.sudox.android.common.helpers.TimeHelperKt"])
class TimeHelperTest : Assert() {

    @Test
    fun testFormatDate_12_hours() {
        val context = Mockito.mock(Context::class.java)

        // 63061200000L - 01.01.1972 00:00
        PowerMockito.mockStatic(System::class.java)
        PowerMockito.mockStatic(DateFormat::class.java)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(63061200000L)
        PowerMockito.`when`(DateFormat.is24HourFormat(context)).thenReturn(false)
        Mockito.`when`(context.getString(R.string.yesterday)).thenReturn("yesterday")

        // Testing & verifying
        assertEquals("yesterday", formatDate(context, 63061200000L - 60000L))

        // 63147600000L - 02.01.1972 00:00, 63061200000L - 01.01.1972 00:00
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(63147600000L)
        assertEquals("yesterday", formatDate(context, 63061200000L))
        assertEquals("00:00 AM", formatDate(context, 63147600000L))
        assertEquals("00:01 AM", formatDate(context, 63147600000L + 60000L))
        assertEquals("00:24 AM", formatDate(context, 63147600000L + 60000L * 24))
        assertEquals("01:00 AM", formatDate(context, 63147600000L + 60000L * 60))
        assertEquals("01:01 AM", formatDate(context, 63147600000L + 60000L * 61))
        assertEquals("01:24 AM", formatDate(context, 63147600000L + 60000L * 84))
        assertEquals("10:00 AM", formatDate(context, 63147600000L + 60000L * 60 * 10))
        assertEquals("10:01 AM", formatDate(context, 63147600000L + 60000L * 60 * 10 + 60000L))
        assertEquals("10:24 AM", formatDate(context, 63147600000L + 60000L * 60 * 10 + 60000L * 24))
        assertEquals("00:24 PM", formatDate(context, 63147600000L + 60000L * 60 * 12 + 60000L * 24))
        assertEquals("04:24 PM", formatDate(context, 63147600000L + 60000L * 60 * 16 + 60000L * 24))
        assertEquals("11:24 PM", formatDate(context, 63147600000L + 60000L * 60 * 23 + 60000L * 24))

        // 68331600000L - 02.01.1972 00:00; 68331600000L - 02.03.1972 00:00
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(68331600000L)
        assertEquals("2 янв", formatDate(context, 63147600000L))
        assertEquals("22 дек 1971", formatDate(context, 62208000000L))

        // Test future time (63061200000L - 01.01.1972 00:00)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(63061200000L)
        assertEquals("2 янв", formatDate(context, 63147600000L))

        // Test next year (62208000000L - 22.12.1971)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(62208000000L)
        assertEquals("1 янв 1972", formatDate(context, 63061200000L))
    }

    @Test
    fun testFormatDate_24_hours() {
        val context = Mockito.mock(Context::class.java)

        // 63061200000L - 01.01.1972 00:00
        PowerMockito.mockStatic(System::class.java)
        PowerMockito.mockStatic(DateFormat::class.java)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(63061200000L)
        PowerMockito.`when`(DateFormat.is24HourFormat(context)).thenReturn(true)
        Mockito.`when`(context.getString(R.string.yesterday)).thenReturn("вчера")

        // Testing & verifying
        assertEquals("вчера", formatDate(context, 63061200000L - 60000L))

        // 63147600000L - 02.01.1972 00:00, 63061200000L - 01.01.1972 00:00
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(63147600000L)
        assertEquals("вчера", formatDate(context, 63061200000L))
        assertEquals("00:00", formatDate(context, 63147600000L))
        assertEquals("00:01", formatDate(context, 63147600000L + 60000L))
        assertEquals("00:24", formatDate(context, 63147600000L + 60000L * 24))
        assertEquals("01:00", formatDate(context, 63147600000L + 60000L * 60))
        assertEquals("01:01", formatDate(context, 63147600000L + 60000L * 61))
        assertEquals("01:24", formatDate(context, 63147600000L + 60000L * 84))
        assertEquals("10:00", formatDate(context, 63147600000L + 60000L * 60 * 10))
        assertEquals("10:01", formatDate(context, 63147600000L + 60000L * 60 * 10 + 60000L))
        assertEquals("10:24", formatDate(context, 63147600000L + 60000L * 60 * 10 + 60000L * 24))
        assertEquals("12:24", formatDate(context, 63147600000L + 60000L * 60 * 12 + 60000L * 24))
        assertEquals("16:24", formatDate(context, 63147600000L + 60000L * 60 * 16 + 60000L * 24))

        // 68331600000L - 02.01.1972 00:00; 68331600000L - 02.03.1972 00:00
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(68331600000L)
        assertEquals("2 янв", formatDate(context, 63147600000L))
        assertEquals("22 дек 1971", formatDate(context, 62208000000L))

        // Test future time (63061200000L - 01.01.1972 00:00)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(63061200000L)
        assertEquals("2 янв", formatDate(context, 63147600000L))

        // Test next year (62208000000L - 22.12.1971)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(62208000000L)
        assertEquals("1 янв 1972", formatDate(context, 63061200000L))
    }
}