package com.sudox.android.common.helpers

import org.junit.Assert
import org.junit.Test

class RegexHelperTest : Assert() {

    @Test
    fun testPhoneRegex() {
        assertTrue(PHONE_REGEX.matches("79674788147"))
        assertFalse(PHONE_REGEX.matches("12345678901"))
        assertFalse(PHONE_REGEX.matches("1234567890123"))
        assertFalse(PHONE_REGEX.matches("12345"))
        assertFalse(PHONE_REGEX.matches("QWERTYUIOP[]ASDFGHJKL;ZXCVBNM,./"))
    }

    @Test
    fun testSmsMessageCodeRegex() {
        assertTrue(SMS_CODE_MESSAGE_REGEX.matches("Sudox: 54789"))
        assertFalse(SMS_CODE_MESSAGE_REGEX.matches("Sudox: 123"))
        assertFalse(SMS_CODE_MESSAGE_REGEX.matches("Sudox: 123456789"))
        assertFalse(SMS_CODE_MESSAGE_REGEX.matches("Sudox: AAAAA"))
        assertFalse(SMS_CODE_MESSAGE_REGEX.matches("AAAAA: 12345"))
    }

    @Test
    fun testNicknameRegex() {
        assertTrue(NICKNAME_REGEX.matches("theMAX"))
        assertTrue(NICKNAME_REGEX.matches("themax"))
        assertTrue(NICKNAME_REGEX.matches("themax"))
        assertTrue(NICKNAME_REGEX.matches("1234567"))
        assertTrue(NICKNAME_REGEX.matches("4"))
        assertTrue(NICKNAME_REGEX.matches("t"))
        assertTrue(NICKNAME_REGEX.matches("W"))
        assertFalse(NICKNAME_REGEX.matches("Ц"))
        assertFalse(NICKNAME_REGEX.matches("б"))
        assertFalse(NICKNAME_REGEX.matches("QWERTYUIOP[]ASDFGHJKL;'ZXCVBNM,./"))
    }

    @Test
    fun testWhitespaceRegex() {
        assertTrue(WHITESPACES_REGEX.matches("  "))
        assertTrue(WHITESPACES_REGEX.matches("   "))
        assertTrue(WHITESPACES_REGEX.matches("    "))
        assertFalse(WHITESPACES_REGEX.matches(" "))
        assertFalse(WHITESPACES_REGEX.matches(""))
    }

    @Test
    fun testNewLineOnStartRegex() {
        assertTrue(NEW_LINE_ON_START_REGEX.matches(" \n"))
        assertFalse(NEW_LINE_ON_START_REGEX.matches("\n "))
        assertFalse(NEW_LINE_ON_START_REGEX.matches("\n"))
    }

    @Test
    fun testNewLineOnEndRegex() {
        assertTrue(NEW_LINE_ON_END_REGEX.matches("\n "))
        assertFalse(NEW_LINE_ON_END_REGEX.matches(" \n"))
        assertFalse(NEW_LINE_ON_END_REGEX.matches("\n"))
    }

    @Test
    fun testNewLineMultipleRegex() {
        assertTrue(NEW_LINE_MULTIPLE_REGEX.matches("\n\n\n"))
        assertFalse(NEW_LINE_MULTIPLE_REGEX.matches("\n\n"))
    }

    @Test
    fun formatMessage() {
        assertEquals("Test", formatMessage("    Test"))
        assertEquals("Test", formatMessage("Test     "))
        assertEquals("Test", formatMessage("    Test     "))
        assertEquals("Test", formatMessage("\nTest"))
        assertEquals("Test", formatMessage("Test\n"))
        assertEquals("Test", formatMessage(" \nTest"))
        assertEquals("Test", formatMessage(" \nTest \n\n\n"))
        assertEquals("Test", formatMessage(" \n\n\nTest \n\n\n"))
        assertEquals("Test\ntest", formatMessage(" \n\n\nTest \n test\n\n\n"))
        assertEquals("Test\n\ntest", formatMessage(" \n\n\nTest \n\n\n test\n\n\n"))
    }

    @Test
    fun formatPhone() {
        assertEquals("789456123", formatPhone("+789456123", false))
        assertEquals("+789456123", formatPhone("+789456123", true))
        assertEquals("+789456123", formatPhone("+789+456123", true))
        assertEquals("789456123", formatPhone("+789+456123", false))
        assertEquals("789456123", formatPhone("789456123", true))
        assertEquals("789456123", formatPhone("789456123", false))
        assertEquals("789456123", formatPhone("Tt789456123", false))
        assertEquals("789456123", formatPhone("Android789456123", false))
        assertEquals("789456123", formatPhone("Android789456123Android", false))
        assertEquals("789456123", formatPhone("Android789456123Android", false))
        assertEquals("789456123", formatPhone("789456123Android", false))
    }
}