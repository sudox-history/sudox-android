package ru.sudox.android.core.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberHelperTest {

    @Test
    fun testNumberFormatting() {
        assertEquals("0", formatNumber(0))
        assertEquals("999", formatNumber(999))
        assertEquals("1K", formatNumber(1_000))
        assertEquals("1,2K", formatNumber(1_200))
        assertEquals("1,2K", formatNumber(1_250))
        assertEquals("1,2K", formatNumber(1_299))
        assertEquals("10K", formatNumber(10_000))
        assertEquals("10,2K", formatNumber(10_299))
        assertEquals("99,9K", formatNumber(99_999))
        assertEquals("999,9K", formatNumber(999_999))
        assertEquals("1M", formatNumber(1_000_000))
        assertEquals("1,2M", formatNumber(1_200_000))
        assertEquals("10,2M", formatNumber(10_200_000))
        assertEquals("100,2M", formatNumber(100_200_000))
        assertEquals("999,2M", formatNumber(999_200_000))
        assertEquals("1B", formatNumber(1_000_000_000))
        assertEquals("1,2B", formatNumber(1_200_000_000))
    }
}